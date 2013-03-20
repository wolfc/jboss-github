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

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OAuthService;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AuthorizationsEditor {
    public static void main(final String[] args) {
        try {
            final LoginDialog dialog = new LoginDialog() {
                @Override
                protected void onCancel() {
                    super.onCancel();
                    System.exit(0);
                }

                @Override
                protected void onOK() {
                    if (getUser() == null && getToken() == null) {
                        JOptionPane.showMessageDialog(this, "Either user/password or OAuthToken must be specified!", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    super.onOK();
                }
            };
            dialog.setModal(true);
            dialog.setAlwaysOnTop(true);
            dialog.pack();
            //dialog.setLocationByPlatform(true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            final GitHubClient client = new GitHubClient();
            if (dialog.getUser() != null) {
                client.setCredentials(dialog.getUser(), new String(dialog.getPassword()));
            } else {
                client.setOAuth2Token(new String(dialog.getToken()));
            }
            final OAuthService oAuthService = new OAuthService(client);
            final List<Authorization> authorizations = oAuthService.getAuthorizations();

            final AuthorizationsForm form = new AuthorizationsForm() {
                @Override
                protected void selected(final int row) {
                    if (row != -1) {
                        final Authorization authorization = authorizations.get(row);
                        setId(authorization.getId());
                        setNote(authorization.getNote());
                        setNoteURL(authorization.getNoteUrl());
                        setScopes(authorization.getScopes());
                        setToken(authorization.getToken());
                    }
                }

                @Override
                protected void save() {
                    try {
                        final Authorization authorization = new Authorization();
                        authorization.setScopes(getScopes());
                        authorization.setNote(getNote());
                        authorization.setNoteUrl(getNoteURL());
                        final int row = authorizations.size();
                        authorizations.add(oAuthService.createAuthorization(authorization));
                        super.save();
                        ((AbstractTableModel) getAuthorizations().getModel()).fireTableRowsInserted(row, row);
                        selected(row);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            // id, note, app name
            final String[] columnNames = { "Id", "Note", "App Name" };
            final Class<?>[] columnClasses = { Integer.class, String.class, String.class };
            form.getAuthorizations().setModel(new AbstractTableModel() {
                @Override
                public int getRowCount() {
                    return authorizations.size();
                }

                @Override
                public int getColumnCount() {
                    return columnNames.length;
                }

                @Override
                public String getColumnName(final int columnIndex) {
                    return columnNames[columnIndex];
                }

                @Override
                public Class<?> getColumnClass(final int columnIndex) {
                    return columnClasses[columnIndex];
                }

                @Override
                public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                    return false;
                }

                @Override
                public Object getValueAt(final int rowIndex, final int columnIndex) {
                    final Authorization authorization = authorizations.get(rowIndex);
                    switch(columnIndex) {
                        case 0:
                            return authorization.getId();
                        case 1:
                            return authorization.getNote();
                        case 2:
                            return authorization.getApp().getName();
                        default:
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
                    throw new UnsupportedOperationException("NYI");
                }
            });

            final JFrame frame = new JFrame("Authorizations Editor");
            frame.setContentPane(form.getContentPane());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
