package com.chtl.mainflow;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Ribath on 4/30/2017.
 */
@DatabaseTable(tableName = "SentMail")
public class SentMail {

    @DatabaseField()
    private String date;

    @DatabaseField()
    private String to;

    @DatabaseField()
    private String subject;

    @DatabaseField()
    private String body;

    public SentMail(String date, String to, String subject, String body) {
        this.date = date;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public SentMail() {
    }

    public String getDate() {
        return date;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
