package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)
class GameViewModel : ViewModel() {
    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() {
            return _word
        }
    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() { return _score }
    private val _timerLiveData = MutableLiveData<Long>()
    private val _gameFinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean>
        get() {
            return _gameFinished
        }
    val currentTimeString = Transformations.map(_timerLiveData) {
        DateUtils.formatElapsedTime(it)
    }

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 100000L
        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    private val timer: CountDownTimer
    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz
    init {
        resetList()
        nextWord()
        _score.value = 0
        _gameFinished.value = false
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onFinish() {
                _timerLiveData.value= DONE
                _eventBuzz.value = BuzzType.GAME_OVER
                _gameFinished.value=true
            }

            override fun onTick(millisUntilFinished: Long) {
                _timerLiveData.value = millisUntilFinished / ONE_SECOND
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }

            }

        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty())
            resetList()
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = _score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = _score.value?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }
    fun onGameFinishComplete() {
        _gameFinished.value = false
    }
    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }
}