package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.models.Period

@Composable
fun BillingUnitsScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val loaded by viewModel.loaded.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val isSetupDone by viewModel.isSetupDone.collectAsState()
    val progress by viewModel.progress.collectAsState()

    val squashResidentialUnits by viewModel.squashResidentialUnits.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isSetupDone) {
            SetupScreen(viewModel, navHostController, modifier)
        } else if (progress) {
            Progress()
        } else if (loaded) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                viewModel.getBillingUnits().forEach { billingUnit ->
                    val serviceStart = billingUnit.servicestart
                    val lastUpdate = billingUnit.lastperiod
                    if (serviceStart != null && lastUpdate != null) {

                        // TODO here we can limit to max years before
                        val periodStart =
                            Period.make((Period(lastUpdate).yearInt() - 1).toString(), 1).period

                        viewModel.getBillingUntitServices(billingUnit.reference.mscnumber)
                            .forEach { service ->
                                val unitOfMeasureSet =
                                    viewModel.getBillingUnitServicesUnitOfMeasure(
                                        billingUnit.reference.mscnumber,
                                        service
                                    )
                                unitOfMeasureSet.forEach { unitOfMeasure ->
                                    if (squashResidentialUnits) {
                                        val selector =
                                            ConsumptionSelector(
                                                billingUnit.reference,
                                                service,
                                                unitOfMeasure,
                                                null,
                                                periodStart = serviceStart
                                            )

                                        BillingUnitCard(
                                            viewModel,
                                            navHostController,
                                            billingUnit,
                                            selector
                                        )
                                    } else {
                                        viewModel.getBillingUntitResidentialUnits(billingUnit.reference.mscnumber)
                                            .forEach { residentialUnit ->
                                                val selector =
                                                    ConsumptionSelector(
                                                        billingUnit.reference,
                                                        service,
                                                        unitOfMeasure,
                                                        residentialUnit,
                                                        periodStart = serviceStart
                                                    )

                                                BillingUnitCard(
                                                    viewModel,
                                                    navHostController,
                                                    billingUnit,
                                                    selector
                                                )
                                            }
                                    }
                                }
                            }
                    }
                }
                Spacer(
                    Modifier.windowInsetsBottomHeight(
                        WindowInsets.navigationBars
                    )
                )
            }
        } else if (loadError) {
            LoadErrorScreen(viewModel, navHostController, modifier)
        } else {
            //back from setgtings without test
            viewModel.reload()
        }
    }
}