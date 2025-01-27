package org.rundeck.tests.functional.selenium.tests.execution

import org.rundeck.tests.functional.selenium.pages.execution.CommandPage
import org.rundeck.tests.functional.selenium.pages.execution.ExecutionShowPage
import org.rundeck.tests.functional.selenium.pages.login.LoginPage
import org.rundeck.util.annotations.SeleniumCoreTest
import org.rundeck.util.container.SeleniumBase

@SeleniumCoreTest
class CommandSpec extends SeleniumBase {

    def setupSpec() {
        setupProject(SELENIUM_BASIC_PROJECT, "/projects-import/${SELENIUM_BASIC_PROJECT}.zip")
    }

    def setup() {
        def loginPage = go LoginPage
        loginPage.login(TEST_USER, TEST_PASS)
    }

    def "abort button in commands page"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test && sleep 45"
            commandPage.runButton.click()
            commandPage.waitForElementAttributeToChange commandPage.runningExecutionStateButton, 'data-execstate', 'RUNNING'
        expect:
            commandPage.abortButton.click()
            commandPage.waitForElementAttributeToChange commandPage.runningExecutionStateButton, 'data-execstate', 'ABORTED'
    }

    def "abort button in show page"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
            def executionShowPage = page ExecutionShowPage
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test && sleep 45"
            commandPage.runButton.click()
            commandPage.runningButtonLink().click()
        expect:
            executionShowPage.waitForElementAttributeToChange executionShowPage.executionStateDisplayLabel, 'data-execstate', 'RUNNING'
            executionShowPage.abortButton.click()
            executionShowPage.waitForElementAttributeToChange executionShowPage.executionStateDisplayLabel, 'data-execstate', 'ABORTED'
    }

    def "default page load shows nodes view"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
            def executionShowPage = page ExecutionShowPage
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href
        expect:
            executionShowPage.validatePage()
            executionShowPage.waitForElementAttributeToChange executionShowPage.executionStateDisplayLabel, 'data-execstate', 'SUCCEEDED'
            executionShowPage.viewContentNodes.isDisplayed()
            !executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "fragment #output page load shows output view"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
            def executionShowPage = page ExecutionShowPage
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href + "#output"
        expect:
            executionShowPage.validatePage()
            executionShowPage.waitForElementAttributeToChange executionShowPage.executionStateDisplayLabel, 'data-execstate', 'SUCCEEDED'
            executionShowPage.viewContentOutput.isDisplayed()
            executionShowPage.viewButtonNodes.isDisplayed()
            !executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "output view toggle to nodes view with button"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
            def executionShowPage = page ExecutionShowPage
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href + "#output"
        expect:
            executionShowPage.validatePage()
            executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonNodes.click()

            executionShowPage.viewContentNodes.isDisplayed()
            !executionShowPage.viewButtonNodes.isDisplayed()
            executionShowPage.viewButtonOutput.isDisplayed()
    }

    def "nodes view toggle to output view with button"() {
        when:
            def commandPage = go CommandPage, SELENIUM_BASIC_PROJECT
            def executionShowPage = page ExecutionShowPage
        then:
            commandPage.nodeFilterTextField.click()
            commandPage.nodeFilterTextField.sendKeys".*"
            commandPage.filterNodeButton.click()
            commandPage.waitForElementToBeClickable commandPage.commandTextField
            commandPage.commandTextField.click()
            commandPage.commandTextField.sendKeys "echo running test '" + this.class.name + "'"
            commandPage.runButton.click()
            def href = commandPage.runningButtonLink().getAttribute("href")
            commandPage.driver.get href
        expect:
            executionShowPage.validatePage()
            executionShowPage.viewButtonOutput.isDisplayed()
            executionShowPage.viewButtonOutput.click()

            executionShowPage.viewContentOutput.isDisplayed()
            !executionShowPage.viewButtonOutput.isDisplayed()
            executionShowPage.viewButtonNodes.isDisplayed()
    }

}
