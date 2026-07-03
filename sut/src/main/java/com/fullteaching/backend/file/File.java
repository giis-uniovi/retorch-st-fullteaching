package com.fullteaching.backend.file;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int type;

    private String name;

    private String nameIdent;

    private String link;

    private int indexOrder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public File() {
    }

    public File(int type, String name) {
        this.type = type; //0: web-link | 1: pdf | 2: video
        this.name = name;
        this.nameIdent = generateNameIdent(name);
        this.link = "";
    }

    public File(int type, String name, String link) {
        this.type = type; //0: web-link | 1: pdf | 2: video
        this.name = name;
        this.nameIdent = generateNameIdent(name);
        this.link = link;
    }

    public File(int type, String name, String link, int indexOrder) {
        this.type = type; //0: web-link | 1: pdf | 2: video
        this.name = name;
        this.nameIdent = generateNameIdent(name);
        this.link = link;
        this.indexOrder = indexOrder;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameIdent() {
        return nameIdent;
    }

    public void setNameIdent(String nameIdent) {
        this.nameIdent = nameIdent;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getIndexOrder() {
        return indexOrder;
    }

    public void setIndexOrder(int indexOrder) {
        this.indexOrder = indexOrder;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof File otherFile)) return false;
        return (otherFile.id == this.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public String getFileExtension() {
        return this.nameIdent.substring(this.nameIdent.lastIndexOf('.') + 1);
    }

    //Generates a string which acts as an identifier for the stored file in the system (local, S3...)
    private String generateNameIdent(String originalName) {
        String s = originalName + SECURE_RANDOM.nextLong();
        s = new BCryptPasswordEncoder().encode(s);
        if (s == null) {
            s = "";
        }
        s = s.replaceAll("[^A-Za-z0-9$]", "");
        int i = originalName.lastIndexOf('.');
        if (i >= 0) {
            // Discard any path-like characters the original extension may contain
            String extension = originalName.substring(i + 1).replaceAll("[^A-Za-z0-9]", "");
            if (!extension.isEmpty()) {
                s += "." + extension;
            }
        }
        return s;
    }

    @Override
    public String toString() {
        return "File[name: \"" + this.name + "\", id: \"" + this.nameIdent + "\", link: \"" + this.link + ", indexOrder: " + this.indexOrder + "]";
    }

}
