package com.dtolabs.rundeck.app.api.homeSummary

import com.dtolabs.rundeck.app.api.marshall.ApiResource
import com.dtolabs.rundeck.app.api.marshall.ElementName
import io.swagger.v3.oas.annotations.media.Schema

@ApiResource
@ElementName("homeSummary")
@Schema
class HomeSummary {
    Integer execCount
    Integer totalFailedCount
    List<String> recentUsers
    List<String> recentProjects
    String frameworkNodeName

    public HomeSummary(Integer execCount, Integer totalFailedCount, List<String> recentUsers,
                       List<String> recentProjects, String frameworkNodeName = [:]) {
        this.execCount = execCount;
        this.totalFailedCount = totalFailedCount;
        this.recentUsers = recentUsers;
        this.recentProjects = recentProjects;
        this.frameworkNodeName = frameworkNodeName;
    }
}
