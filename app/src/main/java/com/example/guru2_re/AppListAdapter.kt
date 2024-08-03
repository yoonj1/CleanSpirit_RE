package com.example.guru2_re

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

class AppListAdapter(private val context: Context, private val appList: MutableList<AppInfo>) : BaseAdapter() {

    private val packageManager: PackageManager = context.packageManager

    override fun getCount(): Int {
        return appList.size
    }

    override fun getItem(position: Int): Any {
        return appList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_app, parent, false)
            holder = ViewHolder().apply {
                appIcon = view.findViewById(R.id.appIcon)
                appName = view.findViewById(R.id.appName)
                appToggle = view.findViewById(R.id.appToggle)
            }
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val appInfo = appList[position]

        try {
            val applicationInfo = packageManager.getApplicationInfo(appInfo.packageName, 0)
            holder.appIcon?.setImageDrawable(packageManager.getApplicationIcon(applicationInfo))
            holder.appName?.text = packageManager.getApplicationLabel(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        holder.appToggle?.isChecked = appInfo.isBlocked

        holder.appToggle?.setOnCheckedChangeListener { _, isChecked ->
            appInfo.isBlocked = isChecked
            saveAppBlockState(appInfo)
        }

        return view
    }

    private fun saveAppBlockState(appInfo: AppInfo) {
        val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(appInfo.packageName, appInfo.isBlocked)
        editor.apply()
    }

    private class ViewHolder {
        var appIcon: ImageView? = null
        var appName: TextView? = null
        var appToggle: Switch? = null
    }
}
