package com.github.bryanx.intellijregexrenamefiles;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

/**
 * @author Bryan de Ridder
 */
public class ReplaceFileNamesDialog extends JDialog
{
  private JPanel contentPane;
  private JTextField textField1;
  private JTextField textField2;
  private JLabel fromLabel;
  private JLabel toLabel;
  private JLabel descriptionLabel;
  private JLabel previewLabel;
  private JCheckBox regexCheckBox;
  private boolean isInputValid = true;
  private Consumer<Boolean> validationListener;

  public ReplaceFileNamesDialog(VirtualFile[] selectedFiles)
  {
    descriptionLabel.setText("Replace text in " + selectedFiles.length + " file names.");
    String firstFileName = selectedFiles[0].getName();
    previewLabel.setText("Preview: " + firstFileName);
    textField1.getDocument().addDocumentListener(new TextFieldChangeListener((e) -> updatePreview(firstFileName)));
    textField2.getDocument().addDocumentListener(new TextFieldChangeListener((e) -> updatePreview(firstFileName)));
    regexCheckBox.addItemListener(e -> updatePreview(firstFileName));
    setContentPane(contentPane);
    setModal(true);
  }

  private void updatePreview(String firstFileName) {
    boolean isValid = true;
    try
    {
      if (isUseRegex())
        previewLabel.setText("Preview: " + firstFileName.replaceAll(getReplaceFromText(), getReplaceToText()));
      else
        previewLabel.setText("Preview: " + firstFileName.replace(getReplaceFromText(), getReplaceToText()));
    } catch (Exception e) {
      previewLabel.setText(e.getMessage());
      isValid = false;
    }
    validationListener.accept(isValid);
  }

  public String getReplaceFromText() {
    return textField1.getText();
  }

  public String getReplaceToText() {
    return textField2.getText();
  }

  public Boolean isUseRegex() {
    return regexCheckBox.isSelected();
  }

  public void listenForValidationChanges(Consumer<Boolean> validationListener)
  {
    this.validationListener = validationListener;
  }
}
