package com.stpauls.dailyliturgy.godsWord

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.localDb.tables.ReadingBean
import com.stpauls.dailyliturgy.others.AppUtils
import com.stpauls.dailyliturgy.databinding.FragmenyTextBinding
import java.util.*


class TextFragment : Fragment(), Observer {

    companion object {
        private val EXTRA_DATA = "extra.data"
        fun newInstance(bean: ReadingBean): TextFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_DATA, bean)
            val fragment = TextFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mBibleActivity: BaseActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mBibleActivity = context as BaseActivity;
    }

    private var _binding: FragmenyTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmenyTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var appView: View
    private var bean: ReadingBean? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appView = view
        bean = arguments?.getSerializable(EXTRA_DATA) as ReadingBean?
        binding.tvReadingTitle?.isSelected = true
        setTextSize(binding.tvReadableText)

        binding.tvReadingTitle.text = bean?.title ?: ""
        AppUtils.setHTMLString(binding.tvReadableText, bean?.description)

        binding.ivFullScreen?.setOnClickListener {
            val dialog = Dialog(
                this@TextFragment.requireContext(),
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
            dialog.setContentView(R.layout.dialog_full_screen_reading_text)
            val ivBack = dialog.findViewById<ImageView>(R.id.ivBack);
            ivBack.setOnClickListener { dialog.dismiss() }
            val tvDialog = dialog.findViewById<TextView>(R.id.tvDialogText);

            AppUtils.setHTMLString(tvDialog, bean?.description)

            setTextSize(tvDialog)
            dialog.show()
        }
        shareReading()
        binding.ivFontToggle?.setOnClickListener {
            mBibleActivity.toggleFont()
        }
    }

    private fun shareReading() {
        val selectedDate = Global.DATE_FORMAT.format(Global.CALENDER.time)
        val readingTitle = bean?.title ?: ""
        val readingBody = AppUtils.getHTMLString(bean?.description )?.toString() ?: ""

        val stringHTMlText = String.format(
            "Shared from Daily Liturgy\n\n%s\n%s\n\n%s",
            selectedDate,
            readingTitle,
            readingBody
        )
        binding.ivReadingShare?.setOnClickListener {

            AppUtils.shareText(mBibleActivity, stringHTMlText)


        }
    }


    override fun onResume() {
        super.onResume()
        val tvView = view?.findViewById<TextView>(R.id.tvReadableText) ?: return
        setTextSize(tvView)
        AppUtils.setHTMLString(tvView, bean?.description)

    }


    override fun update(obsever: Observable?, p1: Any?) {
        val views = view;
        //setTextSize(tvView)

    }

    private fun setTextSize(tvView: TextView?) {

        AppUtils.setFontSize(mBibleActivity, tvView)

    }




}