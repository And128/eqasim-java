package org.eqasim.bavaria.mode_choice.utilities.estimators;

import java.util.List;

import org.eqasim.bavaria.mode_choice.parameters.BavariaModeParameters;
import org.eqasim.core.simulation.modes.drt.mode_choice.predictors.DrtPredictor;
import org.eqasim.core.simulation.mode_choice.utilities.UtilityEstimator;
import org.eqasim.core.simulation.mode_choice.utilities.estimators.EstimatorUtils;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

import com.google.inject.Inject;

public class BavariaDrtUtilityEstimator implements UtilityEstimator {
    private final BavariaModeParameters parameters;
    private final DrtPredictor predictor;

    @Inject
    public BavariaDrtUtilityEstimator(BavariaModeParameters parameters, DrtPredictor predictor) {
        this.parameters = parameters;
        this.predictor = predictor;
    }

    @Override
    public double estimateUtility(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
        var vars = predictor.predictVariables(person, trip, elements);

        double u = 0.0;
        // constant
        u += parameters.drt.alpha_u;
        // in-vehicle travel time (treat like PT)
        u += parameters.drt.betaTravelTime_u_min * vars.travelTime_min;
        // waiting time
        u += parameters.drt.betaWaitingTime_u_min * vars.waitingTime_min;
        // access/egress time (use Bavaria access parameter)
        u += parameters.betaAccessTime_u_min * vars.accessEgressTime_min;
        // monetary cost with distance interaction
        double interaction = EstimatorUtils.interaction(
                vars.euclideanDistance_km,
                parameters.referenceEuclideanDistance_km,
                parameters.lambdaCostEuclideanDistance);
        u += parameters.betaCost_u_MU * interaction * vars.cost_MU;

        return u;
    }
}