package com.example.flashcards

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcards.ui.theme.FlashcardsTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import com.example.flashcards.ui.theme.FlashCard
import com.example.flashcards.ui.theme.generateCards
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

val cardList = generateCards()

class MainActivity : ComponentActivity() {
    @SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardsTheme {
                val offsetX = remember { Animatable(0f) }
                val rotation = remember { Animatable(0f) }
                var currCard by remember {mutableStateOf(0)}
                var isItFlipped by remember { mutableStateOf(false) }

                println("currCard: $currCard")

                Column(
                    modifier = Modifier
                        .background(Color.LightGray)
                ) {
                    Row(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                coroutineScope {
                                    while (true) {
                                        val pointerId = awaitPointerEventScope {
                                            awaitFirstDown().id
                                        }
                                        awaitPointerEventScope {
                                            horizontalDrag(pointerId) {
                                                launch {
                                                    // logic for rotating
                                                    if (
                                                        !isItFlipped && (rotation.value + it.positionChange().x) < 0 && offsetX.value == 0f
                                                        ||
                                                        isItFlipped && (rotation.value + it.positionChange().x) > 0 && offsetX.value == 0f
                                                    ) {
                                                        val rotationMultiplier = 0.19f
                                                        val rotationChange =
                                                            clamp(rotationMultiplier * it.positionChange().x)

                                                        // swiping left to rotate past 90f
                                                        if (rotation.value in 0.0..90.0 && (rotation.value + rotationChange) > 90) {
                                                            isItFlipped = !isItFlipped
                                                            rotation.snapTo(-180 + rotation.value + rotationChange)
                                                        }
                                                        // swiping right to rotate past -90f
                                                        else if (rotation.value > -90 && rotation.value <= 0 && (rotation.value + rotationChange) < -90) {
                                                            isItFlipped = !isItFlipped
                                                            rotation.snapTo(180 + rotation.value + rotationChange)
                                                            // otherwise, just follow the rotationChange
                                                        } else {
                                                            rotation.snapTo(rotation.value + rotationChange)
                                                        }
                                                        // logic for translating
                                                    } else if (
                                                        !isItFlipped && (offsetX.value + it.positionChange().x) > 0 && rotation.value == 0f
                                                        ||
                                                        isItFlipped && (offsetX.value + it.positionChange().x) < 0 && rotation.value == 0f
                                                    ) {
                                                        offsetX.snapTo(offsetX.value + it.positionChange().x)
                                                    }
                                                }
                                            }
                                        }
                                        // logic for animating when the finger is lifted up
                                        launch {
                                            rotation.animateTo(0f)
                                            if (rotation.value == 0f && offsetX.value < -200 && currCard < cardList.size - 1)
                                            {
                                                offsetX.animateTo(-1000f)

                                            } else if (rotation.value == 0f && offsetX.value > 200 && currCard > 0)
                                            {
                                                offsetX.animateTo(1000f)
                                            } else {
                                                offsetX.animateTo(0f)
                                            }
                                        }

                                    }
                                }
                            }
                            .fillMaxSize()
                    ) {

                        LaunchedEffect(offsetX.value) {
                            if (offsetX.value == -1000f && currCard < cardList.size - 1) {
                                currCard += 1
                                offsetX.snapTo(0f)
                                rotation.snapTo(0f)
                                isItFlipped = false
                            }
                            else if (offsetX.value == 1000f && currCard > 0) {
                                currCard -= 1
                                offsetX.snapTo(0f)
                                rotation.snapTo(0f)
                                isItFlipped = true
                            }

                            }


                        // if we've swiped left (moving finger right) and we're not on the first card...

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currCard > 0) {
                                Card(cardList[currCard - 1], true, 0f, offsetX.value - 1000)
                            }
                            Card(cardList[currCard], isItFlipped, rotation.value, offsetX.value)
                            if (currCard < cardList.size - 1) {
                                Card(cardList[currCard + 1], false, 0f, offsetX.value + 1000)
                            }

                        }
                    }

                }

            }
        }
        }

    private fun clamp(value: Float, min: Float = -45f, max: Float = 45f): Float {
        if (value >= max) return max
        if (value <= min) return min
        return value
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Card(flashCard: FlashCard, isItFlipped: Boolean, rotation: Float, offsetX: Float) {
    val text = if (!isItFlipped) flashCard.frontText else flashCard.backText

    Column(
        modifier = Modifier
            .size(250.dp, 400.dp)
            .fillMaxSize()
            ,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    translationX = offsetX
                }
                .background(Color(40, 90, 130), shape = RoundedCornerShape(12.dp))
            ,

        contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 180.dp)
            )
        }
    }
}


