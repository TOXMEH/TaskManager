package taskpack.telegram;

import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import taskpack.model.*;

import java.util.ArrayList;
import java.util.List;

import static taskpack.Application.LOG;

public class TaskListBot extends TelegramLongPollingBot {

    public TaskListBot() {
//        state = "TaskList";
    }

    private static ReplyKeyboardMarkup getTaskListKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Archive");
        keyboardFirstRow.add("Start work");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getFinishingButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Get Statistics");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private static InlineKeyboardMarkup setTaskButtons(int taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineKeyboardButtonList = new ArrayList<>();

        List<InlineKeyboardButton> first = new ArrayList<>();

        inlineKeyboardButtonList.add(first);

        InlineKeyboardButton up = new InlineKeyboardButton();
        InlineKeyboardButton down = new InlineKeyboardButton();
        InlineKeyboardButton remove = new InlineKeyboardButton();
        InlineKeyboardButton sendToArchive = new InlineKeyboardButton();

        first.add(up);
        first.add(down);
        first.add(sendToArchive);
        first.add(remove);

        up.setText("Up");
        up.setCallbackData("up" + taskId);
        down.setText("Down");
        down.setCallbackData("down" + taskId);
        sendToArchive.setText("To Archive");
        sendToArchive.setCallbackData("sendToArchive" + taskId);
        remove.setText("Remove");
        remove.setCallbackData("remove" + taskId);
//        inlineKeyboardButtonList.add(0, up);
//        inlineKeyboardButtonList.add(1, down);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonList);


        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup setFinishingButton(int taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineKeyboardButtonList = new ArrayList<>();

        List<InlineKeyboardButton> first = new ArrayList<>();

        inlineKeyboardButtonList.add(first);

        InlineKeyboardButton changeCompletion = new InlineKeyboardButton();


        first.add(changeCompletion);

        changeCompletion.setText("Change completion");
        changeCompletion.setCallbackData("change_completion" + taskId);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonList);

        return inlineKeyboardMarkup;
    }

    private static ReplyKeyboardMarkup getArchiveKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("Tasklist");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);


        return replyKeyboardMarkup;
    }

    private static InlineKeyboardMarkup setArchiveButtons(int taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineKeyboardButtonList = new ArrayList<>();

        List<InlineKeyboardButton> first = new ArrayList<>();
//        List <InlineKeyboardButton> down = new ArrayList <InlineKeyboardButton>();

        inlineKeyboardButtonList.add(first);

        InlineKeyboardButton remove = new InlineKeyboardButton();
        InlineKeyboardButton sendToArchive = new InlineKeyboardButton();

        first.add(sendToArchive);
        first.add(remove);

        sendToArchive.setText("To TaskList");
        sendToArchive.setCallbackData("returnToTaskList" + taskId);
        remove.setText("Remove");
        remove.setCallbackData("remove_from_archive" + taskId);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonList);

        return inlineKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return "TOXMEHbot";
    }

    @Override
    public String getBotToken() {
        return "248028341:AAEWBxKa7CJKbcCjp_WimE-1P6WZ-q4-8Yw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if ((message.getText().equals("/tasklist")) || (message.getText().equals("Tasklist"))) {
                String chatId = message.getChatId().toString();
                getInitialTasks(chatId);
            } else if ((message.getText().equals("/archive")) || (message.getText().equals("Archive"))) {
                String chatId = message.getChatId().toString();
                getArchiveTasks(chatId);
            } else if ((message.getText().equals("/start_work")) || (message.getText().equals("Start Work"))) {
                String chatId = message.getChatId().toString();
                startWork(chatId);
            } else if ((message.getText().equals("Get Statistics"))) {
                String chatId = message.getChatId().toString();
                getStatistics(chatId);
            } else if (message.getText().contains("-")) {
                addNewTask(message);
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
//            System.out.println(callbackQuery.getData());
            if (callbackQuery.getData().contains("up")) {
                moveTaskUp(callbackQuery);
            } else if (callbackQuery.getData().contains("down")) {
                moveTaskDown(callbackQuery);
            } else if (callbackQuery.getData().contains("sendToArchive")) {
                sendTaskToArchive(callbackQuery);
            } else if (callbackQuery.getData().contains("remove")) {
                removeTask(callbackQuery);
            } else if (callbackQuery.getData().contains("remove_from_archive")) {
                removeTaskFromArchive(callbackQuery);
            } else if (callbackQuery.getData().contains("returnToTaskList")) {
                returnTaskToTaskList(callbackQuery);
            } else if (callbackQuery.getData().contains("change_completion")) {
                changeTaskCompletion(callbackQuery);
            }
        }
    }

    public void moveTaskUp(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(2));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (TaskRepository.getInstance().moveTask(task, task.getPosition() - 1)) {
            getInitialTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void moveTaskDown(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(4));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (TaskRepository.getInstance().moveTask(task, task.getPosition() + 1)) {
            getInitialTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void sendTaskToArchive(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(13));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (TaskList.getInstance().sendTaskToArchive(task)) {
            getInitialTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void returnTaskToTaskList(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(13));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (Archive.getInstance().returnTask(task)) {
            getInitialTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void removeTask(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(6));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (TaskList.getInstance().deleteTask(task)) {
            getInitialTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void removeTaskFromArchive(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(6));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        if (TaskList.getInstance().deleteTask(task)) {
            getArchiveTasks(chatId);
        } else {
            badState(callbackQuery);
        }
    }

    public void changeTaskCompletion(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        int taskId = Integer.parseInt(callbackQuery.getData().substring(17));
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        TaskRepository.getInstance().changeCompletion(taskId);
        finishWork(chatId);
    }

    public void getInitialTasks(String chatId) {
        List<Task> taskList = TaskRepository.getInstance().getUnarchivatedTasks();
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);
        sendMessageRequest.setChatId(chatId); //who should get from the message the sender that sent it.

        SendMessage sendTitle = sendMessageRequest.setText("Task Manager\n");
        sendTitle.setReplyMarkup(getTaskListKeyboard());
        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        taskList
                .stream()
                .forEach((task) -> {
                    SendMessage sendMessage = sendMessageRequest.setText(task.getName() + " <b>"
                            + task.getDuration() + "</b>\n");
                    sendMessage.setReplyMarkup(setTaskButtons(task.getId()));
                    try {
                        sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                    } catch (TelegramApiException e) {
                        LOG.error("TelegramApiException " + e);
                    }
                });
    }

    public void addNewTask(Message message) {
        String chatId = message.getChatId().toString();

        int dividerPositon = message.getText().indexOf("-");
        String name = message.getText().substring(0, dividerPositon);
        int duration = Integer.parseInt(message.getText().substring(dividerPositon + 1).replace(" ", ""));

        Task task = new Task(name, duration);
        TaskList.getInstance().createTask(task);

        getInitialTasks(chatId);


    }

    public void getArchiveTasks(String chatId) {
        List<Task> archiveList = TaskRepository.getInstance().getArchivatedTasks();
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);
        sendMessageRequest.setChatId(chatId); //who should get from the message the sender that sent it.

        SendMessage sendTitle = sendMessageRequest.setText("Task Manager Archive\n");
        sendTitle.setReplyMarkup(getArchiveKeyboard());
        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        archiveList
                .stream()
                .forEach((task) -> {
                    SendMessage sendMessage = sendMessageRequest.setText(task.getName() + " <b>"
                            + task.getDuration() + "</b>\n");

                    sendMessage.setReplyMarkup(setArchiveButtons(task.getId()));
                    try {
                        sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                    } catch (TelegramApiException e) {
                        LOG.error("TelegramApiException " + e);
                    }
                });


    }

    public void badState(CallbackQuery callbackQuery) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        answer.setText("Not acceptable");
        answer.setShowAlert(false);
        try {
            answerCallbackQuery(answer);
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void startWork(String chatId) {
        List<Task> taskList = TaskRepository.getInstance().getUnarchivatedTasks();
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);
        sendMessageRequest.setChatId(chatId); //who should get from the message the sender that sent it.

        SendMessage sendTitle = sendMessageRequest.setText("Task Manager\n");
        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        taskList
                .stream()
                .forEach((task) -> {
                    SendMessage sendMessage = sendMessageRequest.setText(task.getName() + " <b>"
                            + task.getDuration() + "</b>\n");
                    try {
                        sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                    } catch (TelegramApiException e) {
                        LOG.error("TelegramApiException " + e);
                    }
                });

        TaskList.getInstance().startWork();

        int currentTaskPosition = TaskList.getInstance().getCurrentTaskPosition();
        Task currentTask = TaskRepository.getInstance().getTaskByPosition(currentTaskPosition);
        Message timeRest = new Message();
        SendMessage sendMessage = sendMessageRequest.setText(
                "Name of the current task: <b>" + currentTask.getName() + "</b>.\n" +
                        "Time rest: <b>" + WorkDay.getInstance().getMinutesRest()
                        + ":" + WorkDay.getInstance().getSecondsRest() + "</b>");

        try {
            timeRest = sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        int timeRestId = timeRest.getMessageId();


        while (currentTaskPosition < TaskList.getInstance().getSize()) {
            refreshMessage(chatId, timeRestId, currentTask);
            currentTaskPosition = TaskList.getInstance().getCurrentTaskPosition();
            currentTask = TaskRepository.getInstance().getTaskByPosition(currentTaskPosition);
        }

        finishWork(chatId);
    }

    public void refreshMessage(String chatId, int timeRestId, Task currentTask) {
        Message timeRest;
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(timeRestId);
        editMessageText.enableHtml(true);
        try {
            editMessageText.setText(
                    "Name of the current task: <b>" + currentTask.getName() + "</b>.\n" +
                            "Time rest: <b>" + WorkDay.getInstance().getMinutesRest()
                            + ":" + WorkDay.getInstance().getSecondsRest() + "</b>");
        } catch (NullPointerException e) {
            LOG.error(TaskList.getInstance().getCurrentTaskPosition());
            LOG.error(TaskList.getInstance().getSize());
        }
        try {
            timeRest = editMessageText(editMessageText); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }
    }

    public void finishWork(String chatId) {
        List<Task> taskList = TaskRepository.getInstance().getUnarchivatedTasks();
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);
        sendMessageRequest.setChatId(chatId); //who should get from the message the sender that sent it.

        SendMessage sendTitle = sendMessageRequest.setText("Choose completed tasks\n");
        sendTitle.setReplyMarkup(getFinishingButton());
        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        taskList
                .stream()
                .forEach((task) -> {
                    String completion = "";
                    if (task.getCompleted()) {
                        completion = "Completed";
                    } else {
                        completion = "Uncompleted";
                    }
                    SendMessage sendMessage = sendMessageRequest.setText(task.getName() + " <b>"
                            + task.getDuration() + "</b><b>  "
                            + completion + "</b>\n");
                    sendMessage.setReplyMarkup(setFinishingButton(task.getId()));

                    try {
                        sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                    } catch (TelegramApiException e) {
                        LOG.error("!!!!!!!!!!!!!TelegramApiException " + e);
                    }
                });
    }

    public void getStatistics(String chatId) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.enableHtml(true);
        sendMessageRequest.setChatId(chatId); //who should get from the message the sender that sent it.

        SendMessage sendMessage = sendMessageRequest.setText("Today you have worked for "
                + TaskRepository.getInstance().getDurationOfUnarchivatedTasks() + "  minutes.\n You have started working at "
                + WorkDay.getInstance().getStartTime().getHour() + ":" + WorkDay.getInstance().getStartTime().getMinute()
                + ".\n You have completed " + TaskRepository.getInstance().getNumberOfCompletedTasks() + "tasks.\n");

        try {
            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
        } catch (TelegramApiException e) {
            LOG.error("TelegramApiException " + e);
        }

        TaskRepository.getInstance().deleteCompletedTasks();
    }
}