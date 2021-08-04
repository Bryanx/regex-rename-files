import com.intellij.mock.MockVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import nl.bryanderidder.regexrenamefiles.IReplaceFileNamesDialog
import nl.bryanderidder.regexrenamefiles.ReplaceFileNamesViewModel
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

/**
 * @author Bryan de Ridder
 */
class FileCountTests : BasePlatformTestCase() {

    private val mockDialog: IReplaceFileNamesDialog = mock {
        on { replaceFromText } doReturn ""
        on { replaceToText } doReturn ""
    }

    fun test_whenNoFilesArePresent_fileCountShouldBe0() {
        val selectedFiles: List<MockVirtualFile> = listOf()
        val vm = ReplaceFileNamesViewModel(selectedFiles, mockDialog)
        assertEquals(vm.getFiles().size, 0)
    }

    fun test_when2FilesArePresent_fileCountShouldBe2() {
        val file1 = MockVirtualFile("file1")
        val file2 = MockVirtualFile("file2")
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mockDialog)
        assertEquals(vm.getFiles().size, 2)
    }

    fun test_whenNestedFilesArePresent_theyShouldNotBeCountedInitially() {
        val file1 = MockVirtualFile("file1")
        file1.addChild(MockVirtualFile("child1"))
        val file2 = MockVirtualFile("file2")
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mockDialog)
        assertEquals(vm.getFiles().size, 2)
    }

    fun test_whenNestedDirectoriesArePresent_theyShouldNotBeCountedInitially() {
        val file1 = MockVirtualFile("file1")
        file1.addChild(MockVirtualFile("child1"))
        val file2 = MockVirtualFile("file2")
        file2.addChild(MockVirtualFile(true, "dir1"))
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mockDialog)
        assertEquals(vm.getFiles().size, 2)
    }

    fun test_whenNestedFilesAreEnabled_theyShouldBeCounted() {
        val mock = mockDialog.stub {
            on { isRenameNestedFilesSelected } doReturn true
        }
        val file1 = MockVirtualFile("file1")
        file1.addChild(MockVirtualFile("child1"))
        val file2 = MockVirtualFile("file2")
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mock)
        assertEquals(vm.getFiles().size, 3)
    }

    fun test_whenOnlyNestedDirectoriesAreEnabled_nestedFilesShouldNotBeCounted() {
        val mock = mockDialog.stub {
            on { isRenameNestedDirectoriesSelected } doReturn true
        }
        val file1 = MockVirtualFile("file1")
        file1.addChild(MockVirtualFile("child1"))
        val file2 = MockVirtualFile("file2")
        file1.addChild(MockVirtualFile(true, "dir1"))
        file1.addChild(MockVirtualFile(true, "dir2"))
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mock)
        assertEquals(vm.getFiles().size, 4)
    }

    fun test_whenNestedFilesAndDirectoriesAreEnabled_theyShouldBeCounted() {
        val mock = mockDialog.stub {
            on { isRenameNestedDirectoriesSelected } doReturn true
            on { isRenameNestedFilesSelected } doReturn true
        }
        val file1 = MockVirtualFile("file1")
        file1.addChild(MockVirtualFile("child1"))
        file1.addChild(MockVirtualFile("child2"))
        val file2 = MockVirtualFile("file2")
        file1.addChild(MockVirtualFile(true, "dir1"))
        file1.addChild(MockVirtualFile(true, "dir2"))
        file1.addChild(MockVirtualFile(true, "dir3"))
        val vm = ReplaceFileNamesViewModel(listOf(file1, file2), mock)
        assertEquals(vm.getFiles().size, 7)
    }

    fun test_whenDoubleNestedFilesAndDirectoriesAreEnabled_theyShouldBeCounted() {
        val mock = mockDialog.stub {
            on { isRenameNestedDirectoriesSelected } doReturn true
            on { isRenameNestedFilesSelected } doReturn true
        }
        val file1 = MockVirtualFile(true, "file1")
        val child1 = MockVirtualFile(true, "child1")
        child1.addChild(MockVirtualFile("grandChild1"))
        file1.addChild(child1)
        val vm = ReplaceFileNamesViewModel(listOf(file1), mock)
        assertEquals(vm.getFiles().size, 3)
    }
}
