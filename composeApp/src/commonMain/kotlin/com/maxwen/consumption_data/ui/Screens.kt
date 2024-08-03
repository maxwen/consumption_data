package com.maxwen.consumption_data.ui

import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.billing_unit_screen
import consumption_data.composeapp.generated.resources.consumption_screen
import consumption_data.composeapp.generated.resources.settings_screen
import org.jetbrains.compose.resources.StringResource

enum class Screens(val title: StringResource) {
    BillingUnitsScreen(title = Res.string.billing_unit_screen),
    ConsumptionScreen(title = Res.string.consumption_screen),
    SettingsScreen(title = Res.string.settings_screen)
}