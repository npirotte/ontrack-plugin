package net.nemerosa.ontrack.jenkins.extension;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.BuildParametersContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackMultiChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackSingleParameterDefinition;
import org.apache.commons.lang.StringUtils;

import static java.lang.String.format;

@Extension(optional = true)
public class OntrackBuildParametersContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Choice parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackChoiceParameterDefinition ontrackChoiceParameter(Runnable closure) {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

    /**
     * Multiple choice parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackMultiChoiceParameterDefinition ontrackMultipleChoiceParameter(Runnable closure) {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackMultiChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

    /**
     * Single parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackSingleParameterDefinition ontrackSingleParameter(Runnable closure) {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackSingleParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

    /**
     * Last parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackChoiceParameterDefinition ontrackBuildParameter(Runnable closure) {
        OntrackBuildParameterContext context = new OntrackBuildParameterContext();
        executeInContext(closure, context);
        context.validate();
        // Computes the DSL script
        // TODO #52 Use binding variables
        String dsl;
        if (StringUtils.isBlank(context.getPromotion())) {
            dsl = format(
                    "ontrack.branch('%s', '%s').standardFilter(count: %d)",
                    context.getProject(),
                    context.getBranch(),
                    context.getCount()
            );
        } else {
            dsl = format(
                    "ontrack.branch('%s', '%s').standardFilter(count: %d, withPromotionLevel: '%s')",
                    context.getProject(),
                    context.getBranch(),
                    context.getCount(),
                    context.getPromotion()
            );
        }
        // Creates the component
        return new OntrackChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                dsl,
                true,
                context.isUseLabel() ? "label" : "name"
        );
    }

}
