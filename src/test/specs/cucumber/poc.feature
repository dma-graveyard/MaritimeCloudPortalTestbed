Feature: Running Cucumber with Protractor
  As a user of Protractor
  I should be able to use Cucumber
  to run my E2E tests

  @dev
  Scenario: Running Cucumber with Protractor
    Given I run Cucumber with Protractor
    Then it should still do normal tests
    Then it should expose the correct global variables

#  @dev
#  Scenario: Wrapping WebDriver
#    Given I go on "app/index.html#/landingpage"
##    Given I go on "http://juliemr.github.io/protractor-demo/"
#    Then the title should equal "Maritime Cloud Portal"

  @dev
  Scenario: Logging in
#    Given I go on "http://juliemr.github.io/protractor-demo/"
    Given I go on "app/index.html#/landingpage"
#    Given I go on "app/index.html#/"
    When I click on "logout" 
    When I click on "Log In" 
    Then I should see "Username"
    And I should see "Password"

