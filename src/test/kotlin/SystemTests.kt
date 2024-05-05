import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.TestDataProvider
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import nl.bryanderidder.regexrenamefiles.ReplaceFileNamesAction

class SystemTests : BasePlatformTestCase() {

    fun test_action() {
        val action = ReplaceFileNamesAction()
        val context = TestDataProvider(project)
        val event = TestActionEvent.createFromAnAction(action, null, ActionPlaces.UNKNOWN, context::getData)
        event.dataContext
        action.update(event)
    }
}
