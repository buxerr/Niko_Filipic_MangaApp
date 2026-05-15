package hr.algebra.mangaapp.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public final class ComboBoxUtils {

    private ComboBoxUtils() {
    }

    public static <T> void resetWithPrompt(ComboBox<T> comboBox, String promptText) {
        comboBox.setValue(null);
        comboBox.getSelectionModel().clearSelection();
        comboBox.setPromptText(promptText);

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(promptText);
                } else {
                    setText(item.toString());
                }
            }
        });
    }
}
