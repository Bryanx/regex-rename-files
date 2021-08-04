package nl.bryanderidder.regexrenamefiles;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Dialog to rename multiple files
 *
 * @author Bryan de Ridder
 */
public class ReplaceFileNamesDialog extends JDialog implements IReplaceFileNamesDialog {
  private JPanel contentPane;
  private JTextField textField1;
  private JTextField textField2;
  private JLabel fromLabel;
  private JLabel toLabel;
  private JLabel descriptionLabel;
  private JLabel previewLabel;
  private JCheckBox regexCheckBox;
  private JCheckBox renameNestedFilesCheckBox;
  private JCheckBox renameNestedDirectoriesCheckBox;
  private Runnable onUpdateRegexCheckBox;
  private Runnable onUpdateRenameNestedFilesCheckBox;
  private Runnable onUpdateRenameNestedDirectoriesCheckBox;
  private Runnable onUpdateFromTextField;
  private Runnable onUpdateToTextField;

  public ReplaceFileNamesDialog() {
    textField1.getDocument().addDocumentListener(new TextFieldChangeListener((e) -> onUpdateFromTextField.run()));
    textField2.getDocument().addDocumentListener(new TextFieldChangeListener((e) -> onUpdateToTextField.run()));
    regexCheckBox.addItemListener(e -> onUpdateRegexCheckBox.run());
    renameNestedFilesCheckBox.addItemListener(e -> onUpdateRenameNestedFilesCheckBox.run());
    renameNestedDirectoriesCheckBox.addItemListener(e -> onUpdateRenameNestedDirectoriesCheckBox.run());
    setContentPane(contentPane);
    setModal(true);
  }

  @Override
  public @NotNull JRootPane getRootPane() {
    return super.getRootPane();
  }

  @Override
  public @NotNull String getReplaceFromText() {
    return textField1.getText() != null ? textField1.getText() : "";
  }

  @Override
  public @NotNull String getReplaceToText() {
    return textField2.getText() != null ? textField2.getText() : "";
  }

  @Override
  public boolean isUseRegex() {
    return regexCheckBox.isSelected();
  }

  @Override
  public void setDescriptionText(@NotNull String text) {
    descriptionLabel.setText(text);
  }

  @Override
  public void setPreviewText(@NotNull String text) {
    previewLabel.setText(text);
  }

  @Override
  public void onUpdateRegexCheckBox(@NotNull Runnable onUpdateRegexCheckBox) {
    this.onUpdateRegexCheckBox = onUpdateRegexCheckBox;
  }

  @Override
  public void onUpdateRenameNestedFilesCheckBox(@NotNull Runnable onUpdateRenameNestedFilesCheckBox) {
    this.onUpdateRenameNestedFilesCheckBox = onUpdateRenameNestedFilesCheckBox;
  }

  @Override
  public void onUpdateRenameNestedDirectoriesCheckBox(@NotNull Runnable onUpdateRenameNestedDirectoriesCheckBox) {
    this.onUpdateRenameNestedDirectoriesCheckBox = onUpdateRenameNestedDirectoriesCheckBox;
  }

  @Override
  public void setVisibleNestedFilesCheckBox(boolean isVisible) {
    renameNestedFilesCheckBox.setVisible(isVisible);
  }

  @Override
  public void setVisibleNestedDirectoriesCheckBox(boolean isVisible) {
    renameNestedDirectoriesCheckBox.setVisible(isVisible);
  }

  @Override
  public void onUpdateFromTextField(@NotNull Runnable onUpdateFromTextField) {
    this.onUpdateFromTextField = onUpdateFromTextField;
  }

  @Override
  public void onUpdateToTextField(@NotNull Runnable onUpdateToTextField) {
    this.onUpdateToTextField = onUpdateToTextField;
  }

  @Override
  public boolean isRenameNestedFilesSelected() {
    return renameNestedFilesCheckBox.isSelected();
  }

  @Override
  public boolean isRenameNestedDirectoriesSelected() {
    return renameNestedDirectoriesCheckBox.isSelected();
  }
}
