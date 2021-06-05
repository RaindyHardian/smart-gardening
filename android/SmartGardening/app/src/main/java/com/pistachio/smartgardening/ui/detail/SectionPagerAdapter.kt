package com.pistachio.smartgardening.ui.detail

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pistachio.smartgardening.R
import com.pistachio.smartgardening.ui.detail.fragment.DetailFragment
import com.pistachio.smartgardening.ui.detail.fragment.DiseaseFragment

class SectionPagerAdapter(private val mContext: Context, fm: FragmentManager, bundle: Bundle) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var plantBundle: Bundle = bundle
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.details, R.string.disease)
    }

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> DetailFragment()
            1 -> DiseaseFragment()
            else -> Fragment()
        }
        fragment.arguments = plantBundle
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence = mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = TAB_TITLES.size

}