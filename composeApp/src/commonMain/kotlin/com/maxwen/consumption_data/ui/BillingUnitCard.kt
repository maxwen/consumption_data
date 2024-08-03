package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.maxwen.consumption_data.models.ConsumptionSelector
import com.maxwen.consumption_data.models.MainViewModel
import consumption_data.composeapp.generated.resources.Res
import consumption_data.composeapp.generated.resources.avg_amount
import consumption_data.composeapp.generated.resources.heat_device
import consumption_data.composeapp.generated.resources.max_amount
import consumption_data.composeapp.generated.resources.min_amount
import consumption_data.composeapp.generated.resources.sum_amount
import consumption_data.composeapp.generated.resources.water_device
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import org.openapitools.client.models.Service
import org.openapitools.client.models.ServiceConfigurationBillingUnit

@Composable
fun BillingUnitCard(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    billingUnit: ServiceConfigurationBillingUnit,
    selector: ConsumptionSelector,
    modifier: Modifier = Modifier
) {
    val mscnumber = billingUnit.reference.mscnumber
    val minConsumption =
        viewModel.minConsumptionOfUnit(selector);
    val maxConsumption =
        viewModel.maxConsumptionOfUnit(selector);
    val avgConsumption =
        viewModel.avgConsumptionOfUnit(selector);
    val sumConsumption =
        viewModel.sumConsumptionOfUnit(selector);
    val service = selector.service

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        onClick = {
            viewModel.setSelector(selector)
            navHostController.navigate(Screens.ConsumptionScreen.name)
        }
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text(mscnumber, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (service) {
                        Service.HEATING -> {
                            Icon(
                                imageVector = vectorResource(Res.drawable.heat_device),
                                contentDescription = null
                            )
                        }

                        Service.HOT_WATER -> {
                            Icon(
                                imageVector = vectorResource(Res.drawable.water_device),
                                contentDescription = null
                            )
                        }

                        Service.COOLING -> TODO()
                        Service.COLD_WATER -> TODO()
                    }
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = selector.service.toString() + " " + selector.unitOfMeasure.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

            BillingUnitCardRow("Start", (billingUnit.servicestart ?: ""))
            BillingUnitCardRow("Last", (billingUnit.lastperiod ?: ""))

            if (selector.residentialUnit != null) {
                BillingUnitCardRow("Residential", selector.residentialUnit.mscnumber)
            }

            Text("Consumption", style = MaterialTheme.typography.titleMedium)
            if (minConsumption != null) {
                BillingUnitCardRow(
                    "Min",
                    minConsumption.first,
                    value2 = minConsumption.second.toString(), icon = Res.drawable.min_amount
                )
            }
            if (maxConsumption != null) {
                BillingUnitCardRow(
                    "Max",
                    maxConsumption.first,
                    value2 = maxConsumption.second.toString(), icon = Res.drawable.max_amount
                )
            }
            BillingUnitCardRow("Average", avgConsumption.toString(), icon = Res.drawable.avg_amount)
            BillingUnitCardRow("Total", sumConsumption.toString(), icon = Res.drawable.sum_amount)
        }
    }
}


@Composable
fun BillingUnitCardRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    value2: String? = null,
    icon: DrawableResource? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
    ) {
        if (value2 != null) {
            Row(modifier = Modifier.weight(0.6f), verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = vectorResource(icon), contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(key, style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.weight(0.4f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = value2, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Row(modifier = Modifier.weight(0.6f), verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = vectorResource(icon), contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(key, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = value, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}