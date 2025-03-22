package com.test.bookplayer.data

object BooksDataStore {
    val books = listOf(
        AudioBook(
            title = "Ask and It Is Given",
            author = "Esther Hicks, Jerry Hicks",
            cover = "https://marloesdevries.com/wp-content/uploads/2022/05/greatescapewoodlandsnursinghome.jpg",
            description = "Ask and It Is Given, by Esther and Jerry Hicks, which presents the teachings of the nonphysical entity Abraham, will help you learn how to manifest your desires so that you're living the joyous and fulfilling life you deserve.",
            summary = listOf(
                SummaryKeyPoint(
                    description = "The Law of Attraction is the most powerful law in the universe.",
                    audioContent = AudioContent(
                        mediaId = "ask_and_it_is_given_1",
                        uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        mediaMetadata = "3:00",
                    ),
                    textContent = TextContent(
                        description = "The Law of Attraction is the most powerful law in the universe."
                    )
                ),
                SummaryKeyPoint(
                    description = "You are a vibrational being.",
                    audioContent = AudioContent(
                        mediaId = "ask_and_it_is_given_2",
                        uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        mediaMetadata = "3:00",
                    ),
                    textContent = TextContent(
                        description = "You are a vibrational being."
                    )
                ),
                SummaryKeyPoint(
                    description = "You are the creator of your own reality.",
                    audioContent = AudioContent(
                        mediaId = "ask_and_it_is_given_3",
                        uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        mediaMetadata = "3:00",
                    ),
                    textContent = TextContent(
                        description = "You are the creator of your own reality."
                    )
                ),
                SummaryKeyPoint(
                    description = "You are a creator; you create with your every thought.",
                    audioContent = AudioContent(
                        mediaId = "ask_and_it_is_given_4",
                        uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        mediaMetadata = "3:00",
                    ),
                    textContent = TextContent(
                        description = "You are a creator; you create with your every thought."
                    )
                ),
                SummaryKeyPoint(
                    description = "Anything that you can imagine is yours to be or do or have.",
                    audioContent = AudioContent(
                        mediaId = "ask_and_it_is_given_5",
                        uri = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        mediaMetadata = "3:00",
                    ),
                    textContent = TextContent(
                        description = "Anything that you can imagine is yours to be or do or have."
                    )
                ),
            )
        ),
    )
}