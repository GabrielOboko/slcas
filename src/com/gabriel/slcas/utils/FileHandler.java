package com.gabriel.slcas.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.gabriel.slcas.model.Book;
import com.gabriel.slcas.model.ItemStatus;
import com.gabriel.slcas.model.Journal;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.model.LibraryItem;
import com.gabriel.slcas.model.Magazine;
import com.gabriel.slcas.model.Role;
import com.gabriel.slcas.model.UserAccount;

/**
 * Saves and loads library data using a JSON file.
 */
public final class FileHandler {

    private FileHandler() { }

    public static void save(LibraryDatabase db, String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"items\": [\n");
        List<LibraryItem> items = db.getItems();
        for (int i = 0; i < items.size(); i++) {
            LibraryItem it = items.get(i);
            sb.append("    {")
              .append("\"id\":").append(JsonUtil.quote(it.getId())).append(',')
              .append("\"type\":").append(JsonUtil.quote(it.getCategory().name())).append(',')
              .append("\"title\":").append(JsonUtil.quote(it.getTitle())).append(',')
              .append("\"author\":").append(JsonUtil.quote(it.getAuthor())).append(',')
              .append("\"year\":").append(it.getYear()).append(',')
              .append("\"identifier\":").append(JsonUtil.quote(it.getIdentifier())).append(',')
              .append("\"status\":").append(JsonUtil.quote(it.getStatus().name())).append(',')
              .append("\"borrowedBy\":").append(it.getBorrowedByUserId() == null ? "null" : JsonUtil.quote(it.getBorrowedByUserId())).append(',')
              .append("\"borrowDate\":").append(it.getBorrowDate() == null ? "null" : JsonUtil.quote(it.getBorrowDate().toString())).append(',')
              .append("\"dueDate\":").append(it.getDueDate() == null ? "null" : JsonUtil.quote(it.getDueDate().toString()))
              .append('}')
              .append(i < items.size() - 1 ? ",\n" : "\n");
        }
        sb.append("  ],\n  \"users\": [\n");
        List<UserAccount> users = db.getUsers();
        for (int i = 0; i < users.size(); i++) {
            UserAccount u = users.get(i);
            sb.append("    {")
              .append("\"id\":").append(JsonUtil.quote(u.getId())).append(',')
              .append("\"name\":").append(JsonUtil.quote(u.getName())).append(',')
              .append("\"role\":").append(JsonUtil.quote(u.getRole().name())).append(',')
              .append("\"history\":[");
            List<String> hist = u.getBorrowingHistory();
            for (int j = 0; j < hist.size(); j++) {
                sb.append(JsonUtil.quote(hist.get(j)));
                if (j < hist.size() - 1) sb.append(',');
            }
            sb.append("]}").append(i < users.size() - 1 ? ",\n" : "\n");
        }
        sb.append("  ]\n}\n");
        Files.write(Path.of(path), sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    public static void load(LibraryDatabase db, String path) throws IOException {
        String text = Files.readString(Path.of(path), StandardCharsets.UTF_8);
        Map<String, Object> root = JsonUtil.asMap(JsonUtil.parse(text));

        db.getItems().clear();
        db.getUsers().clear();

        for (Object o : JsonUtil.asList(root.get("items"))) {
            Map<String, Object> m = JsonUtil.asMap(o);
            String id = JsonUtil.asString(m.get("id"));
            String type = JsonUtil.asString(m.get("type"));
            String title = JsonUtil.asString(m.get("title"));
            String author = JsonUtil.asString(m.get("author"));
            int year = JsonUtil.asInt(m.get("year"));
            String identifier = JsonUtil.asString(m.get("identifier"));

            LibraryItem item;
            if ("BOOK".equals(type)) item = new Book(id, title, author, year, identifier);
            else if ("MAGAZINE".equals(type)) item = new Magazine(id, title, author, year, identifier);
            else item = new Journal(id, title, author, year, identifier);

            String status = JsonUtil.asString(m.get("status"));
            if (status != null) item.setStatus(ItemStatus.valueOf(status));
            String borrowedBy = JsonUtil.asString(m.get("borrowedBy"));
            item.setBorrowedByUserId(borrowedBy);
            String borrowDate = JsonUtil.asString(m.get("borrowDate"));
            if (borrowDate != null) item.setBorrowDate(LocalDate.parse(borrowDate));
            String dueDate = JsonUtil.asString(m.get("dueDate"));
            if (dueDate != null) item.setDueDate(LocalDate.parse(dueDate));

            db.addItem(item);
            IDGenerator.fastForward(id);
        }

        for (Object o : JsonUtil.asList(root.get("users"))) {
            Map<String, Object> m = JsonUtil.asMap(o);
            String id = JsonUtil.asString(m.get("id"));
            String name = JsonUtil.asString(m.get("name"));
            Role role = Role.valueOf(JsonUtil.asString(m.get("role")));
            UserAccount user = new UserAccount(id, name, role);
            List<Object> hist = JsonUtil.asList(m.get("history"));
            if (hist != null) {
                for (Object h : hist) {
                    user.getBorrowingHistory().add(JsonUtil.asString(h));
                }
            }
            db.addUser(user);
            IDGenerator.fastForward(id);
        }

        // Rebuild each user's borrowed-item list from the loaded item data.
        for (LibraryItem item : db.getItems()) {
            if (item.getBorrowedByUserId() != null) {
                UserAccount u = db.findUserById(item.getBorrowedByUserId());
                if (u != null) u.getCurrentlyBorrowedItemIds().add(item.getId());
            }
        }
    }

    public static boolean exists(String path) {
        return Files.exists(Path.of(path));
    }
}
