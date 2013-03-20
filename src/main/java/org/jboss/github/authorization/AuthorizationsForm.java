/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.github.authorization;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AuthorizationsForm {
    private final Map<String, JCheckBox> scopes = new HashMap<String, JCheckBox>(9, 1);

    private JTable authorizations;
    private JPanel contentPane;
    private JButton newButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel idLabel;
    private JCheckBox userFollowCheckBox;
    private JCheckBox userCheckBox;
    private JCheckBox userEMailCheckBox;
    private JCheckBox publicRepoCheckBox;
    private JCheckBox repoCheckBox;
    private JCheckBox repoStatusCheckBox;
    private JCheckBox deleteRepoCheckBox;
    private JCheckBox notificationsCheckBox;
    private JCheckBox gistCheckBox;
    private JTextField noteField;
    private JTextField noteURLTextField;
    private JButton deleteButton;
    private JTextField tokenTextField;

    public AuthorizationsForm() {
        scopes.put("user", userCheckBox);
        scopes.put("user:email", userEMailCheckBox);
        scopes.put("user:follow", userFollowCheckBox);
        scopes.put("public_report", publicRepoCheckBox);
        scopes.put("repo", repoCheckBox);
        scopes.put("repo:status", repoStatusCheckBox);
        scopes.put("delete_repo", deleteRepoCheckBox);
        scopes.put("notifications", notificationsCheckBox);
        scopes.put("gist", gistCheckBox);

        reset();
        final ListSelectionModel selectedAuthorizations = authorizations.getSelectionModel();
        selectedAuthorizations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedAuthorizations.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                final int index = e.getFirstIndex();
                if (selectedAuthorizations.isSelectionEmpty()) {
                    selected(-1);
                } else {
                    selected(index);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                reset();
            }
        });
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //newButton.setEnabled(false);
                //authorizations.setEnabled(false);
                authorizations.getSelectionModel().clearSelection();
                idLabel.setText("New");
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                cancelButton.setEnabled(true);
                deleteButton.setEnabled(false);
                ce(noteField, noteURLTextField);
                ce(userFollowCheckBox, userCheckBox, userEMailCheckBox, publicRepoCheckBox, repoCheckBox, repoStatusCheckBox, deleteRepoCheckBox, notificationsCheckBox, gistCheckBox);
                tokenTextField.setText("---");
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                save();
            }
        });
    }

    private static void cd(final AbstractButton... buttons) {
        for (final AbstractButton button : buttons) {
            button.setEnabled(false);
            button.setSelected(false);
        }
    }

    private static void cd(final JTextField... textFields) {
        for (final JTextField textField : textFields) {
            textField.setEnabled(false);
            textField.setText("");
        }
    }

    private static void ce(final JCheckBox... checkBoxes) {
        for (final JCheckBox checkBox : checkBoxes) {
            checkBox.setEnabled(true);
            checkBox.setSelected(false);
        }
    }

    private static void ce(final JTextField... textFields) {
        for (final JTextField textField : textFields) {
            textField.setEnabled(true);
            textField.setText("");
        }
    }

    private static void d(final JComponent... components) {
        for (final JComponent component : components) {
            component.setEnabled(false);
        }
    }

    public JTable getAuthorizations() {
        return authorizations;
    }

    public JComponent getContentPane() {
        return contentPane;
    }

    protected String getNote() {
        return ifSet(noteField.getText());
    }

    protected String getNoteURL() {
        return ifSet(noteURLTextField.getText());
    }

    public List<String> getScopes() {
        final List<String> selectedScopes = new ArrayList<String>();
        for (final Map.Entry<String, JCheckBox> entry : scopes.entrySet()) {
            if (entry.getValue().isSelected())
                selectedScopes.add(entry.getKey());
        }
        return selectedScopes;
    }

    private static String ifSet(final String s) {
        if (s.length() == 0)
            return null;
        return s;
    }

    private void reset() {
        //newButton.setEnabled(true);
        //authorizations.setEnabled(true);
        authorizations.getSelectionModel().clearSelection();
        idLabel.setText("---");
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        deleteButton.setEnabled(false);
        cd(noteField, noteURLTextField);
        cd(userFollowCheckBox, userCheckBox, userEMailCheckBox, publicRepoCheckBox, repoCheckBox, repoStatusCheckBox, deleteRepoCheckBox, notificationsCheckBox, gistCheckBox);
        tokenTextField.setText("---");
    }

    protected void save() {
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
//        deleteButton.setEnabled(false);
        d(noteField, noteURLTextField);
        d(userFollowCheckBox, userCheckBox, userEMailCheckBox, publicRepoCheckBox, repoCheckBox, repoStatusCheckBox, deleteRepoCheckBox, notificationsCheckBox, gistCheckBox);
    }

    protected void selected(final int row) {
        if (row == -1)
            reset();
    }

    public void setId(final int id) {
        idLabel.setText(Integer.toString(id));
    }

    public void setNote(final String note) {
        noteField.setText(note);
    }

    public void setNoteURL(final String noteURL) {
        noteURLTextField.setText(noteURL);
    }

    public void setScopes(final Collection<String> selectedScopes) {
        for (final Map.Entry<String, JCheckBox> entry : scopes.entrySet()) {
            if (selectedScopes.contains(entry.getKey()))
                entry.getValue().setSelected(true);
            else
                entry.getValue().setSelected(false);
        }
    }

    public void setToken(final String token) {
        tokenTextField.setText(token);
    }
}
