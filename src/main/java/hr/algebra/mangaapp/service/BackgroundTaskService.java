package hr.algebra.mangaapp.service;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class BackgroundTaskService {

    public <T> void run(
            String title,
            String headerText,
            String progressMessage,
            String cancelMessage,
            String threadName,
            Function<BooleanSupplier, T> backgroundAction,
            Consumer<T> onSuccess,
            Consumer<Throwable> onFailure,
            Runnable onCancel
    ) {
        AtomicBoolean cancelledByUser = new AtomicBoolean(false);

        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return backgroundAction.apply(this::isCancelled);
            }
        };

        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle(title);
        progressAlert.setHeaderText(headerText);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        progressAlert.getButtonTypes().setAll(cancelButtonType);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label progressLabel = new Label(progressMessage);
        VBox progressContent = new VBox(10, progressIndicator, progressLabel);
        progressAlert.getDialogPane().setContent(progressContent);

        progressAlert.setOnCloseRequest(event -> {
            if (task.isRunning()) {
                cancelledByUser.set(true);
                task.cancel();
            }
        });

        task.setOnSucceeded(event -> {
            progressAlert.hide();

            if (!cancelledByUser.get()) {
                onSuccess.accept(task.getValue());
            }
        });

        task.setOnFailed(event -> {
            progressAlert.hide();

            if (!cancelledByUser.get()) {
                onFailure.accept(task.getException());
            }
        });

        task.setOnCancelled(event -> {
            progressAlert.hide();
            onCancel.run();
        });

        Thread taskThread = new Thread(task, threadName);
        taskThread.setDaemon(true);

        Button cancelButton = (Button) progressAlert.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setOnAction(event -> {
            cancelledByUser.set(true);
            progressLabel.setText(cancelMessage);
            task.cancel();
        });

        progressAlert.show();
        taskThread.start();
    }
}
