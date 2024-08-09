package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.MainViewModel
import com.maxwen.consumption_data.models.Period
import com.maxwen.consumption_data.models.icon
import org.jetbrains.compose.resources.vectorResource
import org.openapitools.client.models.Service
import kotlin.math.min

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

    if (!isSetupDone) {
        SetupScreen(viewModel, navHostController, modifier)
    } else if (progress) {
        Progress()
    } else if (loaded) {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val billingUntits = viewModel.getBillingUnits()
            val services = mutableListOf<Service>()
            services.addAll(
                viewModel.getBillingUntitServices(billingUntits).sortedBy { it.toString() })
            if (!viewModel.isShowServicesInit && services.isNotEmpty()) {
                viewModel.setShowServices(
                    services
                )
                viewModel.isShowServicesInit = true
            }
            val showServices by viewModel.showServices.collectAsState()
            val showServicesCopy = mutableListOf<Service>()
            showServicesCopy.addAll(showServices)

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(10.dp))

                for (service in services) {
                    val inShowServices = showServicesCopy.contains(service)
                    Button(
                        contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                        shape = if (inShowServices) ButtonDefaults.shape else ButtonDefaults.filledTonalShape,
                        colors = if (inShowServices) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                        elevation = if (inShowServices) ButtonDefaults.buttonElevation() else ButtonDefaults.filledTonalButtonElevation(),
                        onClick = {
                            if (showServicesCopy.contains(service)) {
                                showServicesCopy.remove(service)
                            } else {
                                showServicesCopy.add(service)
                                showServicesCopy.sort()
                            }
                            viewModel.setShowServices(showServicesCopy)
                        }) {
                        Icon(
                            imageVector = vectorResource(service.icon()),
                            contentDescription = service.toString()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
            billingUntits.forEach { billingUnit ->
                val serviceStart = billingUnit.servicestart
                val lastUpdate = billingUnit.lastperiod
                if (serviceStart != null && lastUpdate != null) {

                    // TODO here we can limit to max years before
                    val periodStart =
                        Period.make((Period(lastUpdate).yearInt() - 1).toString(), 1).period

                    viewModel.getBillingUntitServices(billingUnit.reference.mscnumber)
                        .filter { showServices.contains(it) }.forEach { service ->
                            if (showServices.contains(service)) {
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