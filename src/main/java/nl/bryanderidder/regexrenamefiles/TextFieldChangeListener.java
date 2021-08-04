package nl.bryanderidder.regexrenamefiles;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

/**
 * TextFieldChangeListener class to prevent having to override all methods
 *
 * @author Bryan de Ridder
 */
public class TextFieldChangeListener implements DocumentListener
{
  private final Consumer<DocumentEvent> onTextChanged;

  public TextFieldChangeListener(Consumer<DocumentEvent> onTextChanged)
  {
    this.onTextChanged = onTextChanged;
  }

  @Override public void insertUpdate(DocumentEvent e)
  {
    onTextChanged.accept(e);
  }

  @Override public void removeUpdate(DocumentEvent e)
  {
    onTextChanged.accept(e);
  }

  @Override public void changedUpdate(DocumentEvent e)
  {
    onTextChanged.accept(e);
  }
}
