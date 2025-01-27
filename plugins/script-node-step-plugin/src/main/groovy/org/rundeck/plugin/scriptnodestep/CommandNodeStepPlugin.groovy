package org.rundeck.plugin.scriptnodestep

import com.dtolabs.rundeck.core.common.INodeEntry
import com.dtolabs.rundeck.core.dispatcher.DataContextUtils
import com.dtolabs.rundeck.core.execution.ExecArgList
import com.dtolabs.rundeck.core.plugins.PluginException
import com.dtolabs.rundeck.core.plugins.PluginResourceLoader
import com.dtolabs.rundeck.core.utils.OptsUtil
import org.rundeck.core.execution.ExecCommand
import com.dtolabs.rundeck.core.execution.service.NodeExecutorResult
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepException
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty
import com.dtolabs.rundeck.plugins.step.NodeStepPlugin
import com.dtolabs.rundeck.plugins.step.PluginStepContext

@Plugin(service = ServiceNameConstants.WorkflowNodeStep, name = EXEC_COMMAND_TYPE)
@PluginDescription(title = "Command", description = "Execute a remote command", isHighlighted = true, order = 0)
class CommandNodeStepPlugin extends ScriptProxyRunner implements NodeStepPlugin, ExecCommand, PluginResourceLoader {

    @PluginProperty(title = "Command",
            description = "Enter the shell command, e.g.: echo this is a test",
            required = true)
    String adhocRemoteString;

    @Override
    void executeNodeStep(PluginStepContext context, Map<String, Object> configuration, INodeEntry entry) throws NodeStepException {
        boolean featureQuotingBackwardCompatible = Boolean.valueOf(context.getExecutionContext().getIFramework()
                .getPropertyRetriever().getProperty("rundeck.feature.quoting.backwardCompatible"));
        def arr = OptsUtil.burst(adhocRemoteString)
        NodeExecutorResult nodeExecutorResult =  context.getFramework().getExecutionService().executeCommand(
                context.getExecutionContext(),
                ExecArgList.fromStrings(featureQuotingBackwardCompatible, DataContextUtils
                .stringContainsPropertyReferencePredicate, arr),
                entry
        );

        if(nodeExecutorResult.resultCode != 0){
            throw new NodeStepException( nodeExecutorResult.failureMessage, nodeExecutorResult.failureReason, entry.getNodename())
        }
    }

    @Override
    List<String> listResources() throws PluginException, IOException {
        ['WorkflowNodeStep.exec-command.icon.png']
    }

    @Override
    InputStream openResourceStreamFor(String name) throws PluginException, IOException {
        return this.getClass().getResourceAsStream("/" + name)
    }
}
