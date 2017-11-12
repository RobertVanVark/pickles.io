# Pickles.io
Pickles is an extension on the popular Behaviour Driven Development framework [Cucumber](https://cucumber.io).

Ideally, you want your Gherkin scenarios to describe a complete feature from the user’s perspective. In complex application landscapes, however, executing such a complete feature can take up significant time. This can be caused by different reasons: technical design decisions, heavy number crunching or just complex business processes that involve different systems or even organizations.

Pickles provides:
-	A syntax to describe time dependencies in your Gherkin scenarios, using **Then after** steps to identify that a verification can only be executed after a certain time.
-	Pause the execution of your scenarios when a step is encountered that cannot yet be verified.
-	Restore the state of your paused scenarios at the right moment and continue your scenario’s execution.

Basically, we’re storing gherkins for later use. Hence, the name Pickles !

All this is implemented in such a way, that you can reuse your existing step definitions without any changes. Pickles will take care of rewriting the stepdefs with time dependencies into regular step definitions so that they can be executed by vanilla Cucumber.

More information can be found on the DevOn [website](https://devon.nl/test-automation), including a detailed [whitepaper](https://devon.nl/kennis/whitepapers) on the subject (unfortunately in Dutch only at the moment).

Pickles is provided as open source under the MIT license. You’re more than welcome to contribute to the project.

