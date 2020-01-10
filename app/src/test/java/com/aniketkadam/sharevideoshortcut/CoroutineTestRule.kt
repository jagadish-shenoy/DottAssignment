package com.aniketkadam.sharevideoshortcut

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 *Code from: https://gist.githubusercontent.com/AniketSK/0fd48da9ed969eee307f92457115612a/raw/5282fedaba87507f567a3ae65256bff973c0eb9a/CoroutineTestRule.kt
 */
class CoroutineTestRule(private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

}
