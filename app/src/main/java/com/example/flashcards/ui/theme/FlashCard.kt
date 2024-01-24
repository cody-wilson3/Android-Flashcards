package com.example.flashcards.ui.theme

    class FlashCard(val front: String, val back: String, var cardIsFlipped: Boolean) {
        val frontText = front
        val backText = back
        var cardFlipped = cardIsFlipped
    }


fun generateCards(): List<FlashCard> {
    val cardList = mutableListOf<FlashCard>()

    val spanishWords = listOf(
        "Hola" to "Hello",
        "Adiós" to "Goodbye",
        "Gracias" to "Thank you",
        "Por favor" to "Please",
        "Sí" to "Yes",
        "No" to "No",
        "¿Cómo estás?" to "How are you?",
        "Buenos días" to "Good morning",
        "Buenas tardes" to "Good afternoon",
        "Buenas noches" to "Good night",
        "Perdón" to "Excuse me",
        "Hasta luego" to "See you later",
        "Mucho gusto" to "Nice to meet you",
        "Lo siento" to "I'm sorry",
        "De nada" to "You're welcome",
        "¿Cuánto cuesta?" to "How much does it cost?",
        "¿Dónde está?" to "Where is it?",
        "Me gusta" to "I like it",
        "No comprendo" to "I don't understand"
    )

    spanishWords.map { (spanish, english) ->
        cardList.add(FlashCard(spanish, english, false))

    }

    return cardList
}




