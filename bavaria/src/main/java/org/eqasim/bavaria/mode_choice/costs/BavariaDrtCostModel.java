package org.eqasim.bavaria.mode_choice.costs;

import java.util.List;

import org.eqasim.bavaria.mode_choice.utilities.predictors.BavariaPersonPredictor;
import org.eqasim.bavaria.mode_choice.utilities.variables.BavariaPersonVariables;
import org.eqasim.core.simulation.mode_choice.cost.CostModel;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.drt.routing.DrtRoute;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.core.router.TripStructureUtils;

import com.google.inject.Inject;

public class BavariaDrtCostModel implements CostModel {
	private final BavariaPersonPredictor personPredictor;

	@Inject
	public BavariaDrtCostModel(BavariaPersonPredictor personPredictor) {
		this.personPredictor = personPredictor;
	}

	@Override
	public double calculateCost_MU(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
		BavariaPersonVariables personVariables = personPredictor.predictVariables(person, trip, elements);

		int drtLegCount = 0;
		for (Leg leg : TripStructureUtils.getLegs(elements)) {
			if (leg.getRoute() instanceof DrtRoute) {
				drtLegCount += 1;
			}
		}

		if (drtLegCount == 0) {
			return 0.0;
		}

		final double pricePerTrip_EUR = 1.9;
		if (personVariables.hasSubscription) {
			// One DRT trip is free with subscription; all additional DRT trips cost 1.90 EUR
			return Math.max(0, drtLegCount - 1) * pricePerTrip_EUR;
		} else {
			return drtLegCount * pricePerTrip_EUR;
		}
	}
}