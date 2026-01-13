package com.diego.playlistmaker.media.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diego.playlistmaker.media.ui.fragments.PlayListsFragment
import com.diego.playlistmaker.media.ui.fragments.TracksFragment

class PagerAdapter(hostFragment: FragmentActivity) : FragmentStateAdapter(hostFragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) TracksFragment() else PlayListsFragment()
    }
}