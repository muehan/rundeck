package org.rundeck.app.data.job.metadata

import com.dtolabs.rundeck.core.authorization.AuthContextProcessor
import com.dtolabs.rundeck.core.authorization.UserAndRolesAuthContext
import groovy.transform.CompileStatic
import org.rundeck.app.components.jobs.ComponentMeta
import org.rundeck.app.components.jobs.JobMetadataComponent
import org.rundeck.app.data.model.v1.job.JobData
import org.rundeck.app.data.model.v1.job.JobDataSummary
import org.rundeck.app.data.providers.v1.job.JobDataProvider
import org.rundeck.core.auth.AuthConstants
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class JobAuthorizationMetadataComponent implements JobMetadataComponent {
    static final String NAME = 'authz'
    public static final Set<String> JOB_AUTH_CHECK_SET = Collections.unmodifiableSet(
        [
            AuthConstants.ACTION_VIEW,
            AuthConstants.ACTION_READ,
            AuthConstants.ACTION_RUN,
            AuthConstants.ACTION_UPDATE,
            AuthConstants.ACTION_CREATE,
            AuthConstants.ACTION_DELETE,
            AuthConstants.ACTION_TOGGLE_EXECUTION,
            AuthConstants.ACTION_TOGGLE_SCHEDULE,
        ].toSet()
    )

    @Autowired
    JobDataProvider jobDataProvider
    @Autowired
    AuthContextProcessor rundeckAuthContextProcessor

    @Override
    Set<String> getAvailableMetadataNames() {
        return [NAME].toSet()
    }

    @Override
    Optional<List<ComponentMeta>> getMetadataForJob(final String id,String project, final Set<String> names, UserAndRolesAuthContext authContext) {
        if (!names.contains(NAME) && !names.contains('*')) {
            return Optional.empty()
        }
        def job = jobDataProvider.findBasicByUuid(id)
        return job.map {
            [ComponentMeta.with(NAME, getAuthzMeta(it, authContext))]
        }
    }

    @Override
    Optional<List<ComponentMeta>> getMetadataForJob(
        final JobDataSummary job,
        final Set<String> names,
        final UserAndRolesAuthContext authContext
    ) {
        if (!names.contains(NAME) && !names.contains('*')) {
            return Optional.empty()
        }
        return Optional.of([ComponentMeta.with(NAME, getAuthzMeta(job, authContext))])
    }

    Map<String, Object> getAuthzMeta(JobData jobDataSummary, UserAndRolesAuthContext authContext) {
        def job = rundeckAuthContextProcessor
            .authResourceForJob(jobDataSummary.jobName, jobDataSummary.groupPath, jobDataSummary.uuid)
        getAuthzMeta(job, jobDataSummary.project, authContext)
    }

    Map<String, Object> getAuthzMeta(JobDataSummary jobDataSummary, UserAndRolesAuthContext authContext) {
        def job = rundeckAuthContextProcessor
            .authResourceForJob(jobDataSummary.jobName, jobDataSummary.groupPath, jobDataSummary.uuid)
        getAuthzMeta(job, jobDataSummary.project, authContext)
    }

    Map<String, Object> getAuthzMeta(
        Map<String, String> authResource,
        String project,
        UserAndRolesAuthContext authContext
    ) {
        def authz = rundeckAuthContextProcessor.authorizeProjectResources(
            authContext,
            [authResource].toSet(),
            JOB_AUTH_CHECK_SET,
            project
        )
        return authz.collectEntries { [it.action, it.authorized] } as Map<String, Object>
    }
}
