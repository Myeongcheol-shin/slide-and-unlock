package com.shino72.slide_and_unlock

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.shino72.slide_and_unlock.ui.theme.SlideandunlockTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlideandunlockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Track()
                }
            }
        }
    }
}

enum class Position{
    Start,End
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun Track(modifier: Modifier = Modifier)
{
    var componentSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val density = LocalDensity.current

    val state = remember {
        AnchoredDraggableState(
            initialValue = Position.Start,
            positionalThreshold = {totalDistance : Float -> totalDistance * 0.5f },
            velocityThreshold = { Float.MAX_VALUE },
            animationSpec = tween(),
        )
    }
    LaunchedEffect(componentSize) {
        if(componentSize.width > 0) {
            val endPosition = with(density){(componentSize.width - 40.dp.toPx() - 16.dp.toPx())}
            state.updateAnchors(
                DraggableAnchors {
                    Position.Start at -0f
                    Position.End at endPosition
                }
            )
        }
    }
    var swipeText by remember {
        mutableStateOf("밀어서 잠금 해제")
    }

    LaunchedEffect(state.currentValue) {
        if(state.currentValue == Position.Start && state.offset > componentSize.width.toFloat() / 2) {
            state.snapTo(Position.End)
        }

        if(state.offset > componentSize.width.toFloat() / 2) {
            swipeText = "잠금 해제 완료"
        }
        else {
            swipeText = "밀어서 잠금 해제"
        }
    }

    val backgroundColor by animateColorAsState(targetValue =
        if(state.offset > componentSize.width.toFloat() / 2) {
            Color.LightGray
        }
        else Color.Gray
        , animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(30.dp))
                .align(Alignment.Center)
                .onGloballyPositioned {
                    componentSize = it.size
                }
        ){
            Text(
                text = swipeText,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        val safeOffset = if(state.offset.isNaN()) 0f else state.offset

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset((safeOffset.roundToInt()), 0)
                }
                .padding(horizontal = 8.dp)
        ){
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = Color.Black)
                    .anchoredDraggable(
                        state = state,
                        orientation = Orientation.Horizontal
                    )
            ){
                Icon(Icons.Outlined.KeyboardArrowRight, "", tint = Color.White, modifier = Modifier.align(
                    Alignment.Center
                ))
            }
        }
    }
}

