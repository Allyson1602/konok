package com.example.konok.ui.myBooks

import MyBooksPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.konok.R

class MyBooksFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_books, container, false)

        val viewPager: ViewPager2 = rootView.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = rootView.findViewById(R.id.tab_layout)

        val adapter = MyBooksPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Lidos"
                1 -> "Possuídos"
                else -> null
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val fragment = parentFragmentManager.findFragmentByTag("f$position")
                when (position) {
                    0 -> (fragment as? ReadBooksFragment)?.loadBooks("read_list")
                    1 -> (fragment as? OwnedBooksFragment)?.loadBooks("owned_list")
                }
            }
        })

        return rootView
    }

    override fun onResume() {
        super.onResume()
        val viewPager: ViewPager2? = view?.findViewById(R.id.view_pager)
        val position = viewPager?.currentItem ?: 0
        val fragment = parentFragmentManager.findFragmentByTag("f$position")
        when (position) {
            0 -> (fragment as? ReadBooksFragment)?.loadBooks("read_list")
            1 -> (fragment as? OwnedBooksFragment)?.loadBooks("owned_list")
        }
    }
}
