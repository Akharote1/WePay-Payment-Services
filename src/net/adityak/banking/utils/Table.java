package net.adityak.banking.utils;

import com.bethecoder.ascii_table.ASCIITable;

import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String title;
    private ArrayList<Row> rows = new ArrayList<>();
    private Row headings;

    public static Table create() {
        return new Table();
    }

    public Table setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Table addRow(Object... columns) {
        Row row = new Row();
        row.items = Arrays.stream(columns).map(Object::toString).toArray(String[]::new);
        rows.add(row);
        return this;
    }

    public Table setHeading(Object... columns) {
        Row row = new Row();
        row.items = Arrays.stream(columns).map(Object::toString).toArray(String[]::new);
        headings = row;
        return this;
    }

    public String[][] getData() {
        String[][] data = new String[rows.size()][rows.size() == 0 ? 0 : rows.get(0).items.length];

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).items.length; j++) {
                data[i][j] = rows.get(i).items[j];
            }
        }

        return data;
    }

    public String toString() {
        String table = ASCIITable.getInstance().getTable(
                headings.items,
                getData()
        );

        int tableWidth = table.split("\n")[0].length();

        StringBuilder out = new StringBuilder();
        if (title != null) {
            int extraChars = Math.max(tableWidth - 4 - title.length(), 0) / 2;
            out.append("+" + "=".repeat(tableWidth - 2) + "+\n");
            out.append(String.format("| " + " ".repeat(extraChars)
                    + "%-" + (tableWidth - 4 - extraChars) + "s |\n", title));
        }
        out.append(table);

        return out.toString();
    }

    public void print() {
        System.out.print(this);
    }
}

class Row {
    public String[] items;
}