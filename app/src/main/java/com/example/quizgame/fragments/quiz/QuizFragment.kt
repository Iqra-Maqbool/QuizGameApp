package com.example.quizgame.fragments.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizgame.ext.showToast
import com.example.quizgame.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {
    private lateinit var binding: FragmentQuizBinding
    private val args: QuizFragmentArgs by navArgs()
    private val quizViewModel: QuizViewModel by viewModels()
    private var currentQuestion = 0
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleBackPress()
        observeViewModelData()
        setupQuizUI()
        setupOptionClickListeners()
    }


    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }


    private fun observeViewModelData() {
        quizViewModel.apply {
            username.observe(viewLifecycleOwner) { username ->
                username?.let { binding.ShowName.text = it }
            }

            message.observe(viewLifecycleOwner) { error ->
                error?.let { requireContext().showToast(it) }
            }

            coins.observe(viewLifecycleOwner) { coins ->
                binding.CoinTextView.text = coins.toString()
            }

            questions.observe(viewLifecycleOwner) { questionsList ->
                if (questionsList.isNotEmpty()) {
                    updateQuestionUI(questionsList)
                }
            }

            fetchUsername()
            fetchCoins()
            fetchQuestions(args.QuestionType)
            fetchSpinChances()
        }
    }


    private fun setupQuizUI() {
        binding.QuizImg.setImageResource(args.QuizImg)
    }


    private fun setupOptionClickListeners() {
        binding.apply {
            Option1.setOnClickListener { handleAnswerSelection(Option1.text.toString()) }
            Option2.setOnClickListener { handleAnswerSelection(Option2.text.toString()) }
            Option3.setOnClickListener { handleAnswerSelection(Option3.text.toString()) }
            Option4.setOnClickListener { handleAnswerSelection(Option4.text.toString()) }
        }
    }


    private fun handleAnswerSelection(selectedAnswer: String) {
        val questionsList = quizViewModel.questions.value ?: return
        if (selectedAnswer == questionsList[currentQuestion].Answer) {
            score += 1
        }
        currentQuestion++
        if (currentQuestion < questionsList.size) {
            updateQuestionUI(questionsList)
        } else {
            handleQuizCompletion()
        }
    }


    private fun updateQuestionUI(questionsList: List<QuizModelClass>) {
        val question = questionsList[currentQuestion]
        binding.apply {
            Question.text = question.Question
            Option1.text = question.Option1
            Option2.text = question.Option2
            Option3.text = question.Option3
            Option4.text = question.Option4
        }
    }


    private fun handleQuizCompletion() {
        if (score >= 4) {
            quizViewModel.updateSpinChances()
            showQuizResult(isWinner = true)
        } else {
            showQuizResult(isWinner = false)
        }
    }


    private fun showQuizResult(isWinner: Boolean) {
        binding.apply {
            quizlayout.visibility = View.GONE
            if (isWinner) {
                winner.visibility = View.VISIBLE
            } else {
                loser.visibility = View.VISIBLE
            }
        }
    }
}
