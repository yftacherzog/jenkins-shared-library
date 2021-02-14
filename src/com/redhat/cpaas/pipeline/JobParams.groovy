package com.redhat.cpaas.pipeline

import hudson.model.Run
import com.cloudbees.groovy.cps.NonCPS
import hudson.model.ParametersAction
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
import hudson.model.Action
import hudson.model.StringParameterValue
import hudson.model.ParameterValue

/**
 * Sets a build parameter on a job at runtime
 * <p>
 * This is useful for storing data with easy access through the RunWrapper.
 *
 * @param currentBuild A RunWrapper object to add the parameter to
 * @param key          The name of the parameter to store
 * @param value        A string value to store in the parameter
 */
@NonCPS
static void modifySetParam(RunWrapper currentBuild, String key, String value) {
    Run build = currentBuild.rawBuild
    List paramsList = new ArrayList<StringParameterValue>()
    paramsList.add(new StringParameterValue(key, value))
    Action newParamsAction = null
    Action oldParamsAction = build.getAction(ParametersAction.class)
    if (oldParamsAction != null) {
        // We need to keep old params
        build.actions.remove(oldParamsAction)
        newParamsAction = oldParamsAction.createUpdated(paramsList)
    } else {
        newParamsAction = new ParametersAction(paramsList)
    }
    build.actions.add(newParamsAction)
}

/**
 * Stores a list of values in a set of parameters
 * <p>
 * Given a prefix, creates a <code>${prefix}_COUNT</code> parameter that
 * contains the amount of values stored. And an indexed
 * <code>${prefix}_N</code> parameter for each stored value, where
 * <code>N</code> is the index number.
 * <p>
 * For example, given the list <code>['A', 'B']</code>, and the prefix
 * '<code>FOO</code>' the following parameters will be created:
 * <ul>
 * <li><code>FOO_COUNT</code> with the value of '<code>2</code>'
 * <li><code>FOO_0</code> with the value of '<code>A</code>'
 * <li><code>FOO_1</code> with the value of '<code>B</code>'
 * </ul>
 *
 * @param build  A RunWrapper object to add the parameters to
 * @param prefix The prefix for the names of the parameters to create
 * @param list   The lsit of values to store
 */
@NonCPS
static void storeListInParams(RunWrapper build, String prefix, Iterable list) {
    JobParams.modifySetParam(build, "${prefix}_COUNT", list.size as String)
    list.eachWithIndex { item, idx ->
        JobParams.modifySetParam(build, "${prefix}_$idx", item as String)
    }
}

/**
 * Reads a parameter value from a build (RunWrapper) object
 * <p>
 * This is useful in case the build in qustion is not the currently running
 * one, in which case the parameters are not available via the
 * '<code>params</code>' or the '<code>env</code>' maps.
 *
 * @param currentBuild A RunWrapper object to read the parameter from
 * @param key          The name of the parameter to read
 * @param defv         A default value to return if the parameter does not
 *                     exist in the build objet
 * @return The requested parameter value if the parameter exists in the build
 *         object, otherwise, the value in 'defv' if its given or null if not.
 */
@NonCPS
static Object getParameterValue(RunWrapper currentBuild, String name, Object defv=null) {
    Run build = currentBuild.rawBuild
    Action parametersAction = build?.getAction(ParametersAction)
    ParameterValue parameterValue = parametersAction?.getParameter(name)
    def value = parameterValue?.getValue()
    return (value == null) ? defv : value
}

/**
 * Reads a list of values that was stored by
 * {@link #storeListInParams(RunWrapper, String, List)} from a build object
 *
 * @param build  A RunWrapper object to read values from
 * @param prefix The prefix for the names of the parameters to read
 * @return A list of values stored in the build object. If the
 *         '<code>${prefix}_COUNT</code> paremeter is missing, an empty list is
 *         returned, If a '<code>${prefix}_N</code>' parameter is missing, a
 *         null is placed in the list instead.
 */
@NonCPS
static Iterable getListFromParams(RunWrapper build, String prefix) {
    def sz = getParameterValue(build, "${prefix}_COUNT", 0) as int
    return (0..<sz).collect { idx ->
        getParameterValue(build, "${prefix}_$idx")
    }
}
