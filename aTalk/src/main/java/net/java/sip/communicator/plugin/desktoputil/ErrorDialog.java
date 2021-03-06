/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.sip.communicator.plugin.desktoputil;

import net.java.sip.communicator.util.Logger;
import net.java.sip.communicator.util.skin.Skinnable;

import org.atalk.android.R;
import org.atalk.android.aTalkApp;
import org.atalk.android.util.java.awt.*;
import org.atalk.android.util.java.awt.event.ActionEvent;
import org.atalk.android.util.java.awt.event.ActionListener;
import org.atalk.android.util.javax.swing.*;

import java.io.*;

/**
 * Implements a <tt>JDialog</tt> which displays an error message and,
 * optionally, a <tt>Throwable</tt> stack trace. <tt>ErrorDialog</tt> has an OK
 * button which dismisses the message and a link to display the
 * <tt>Throwable</tt> stack trace upon request if available.
 *
 * @author Yana Stamcheva
 * @author Adam Netocny
 * @author Lyubomir Marinov
 * @author Eng Chong Meng
 */
public class ErrorDialog extends SIPCommDialog implements ActionListener, /* HyperlinkListener,*/ Skinnable
{
    public static final int ERROR = 64;
    /**
     * The type of <tt>ErrorDialog</tt> which displays a warning instead of an error.
     */
    public static final int WARNING = 1;
    private static final long serialVersionUID = 1L;

    /**
     * The <tt>Logger</tt> used by the <tt>ErrorDialog</tt> class and its
     * instances for logging output.
     */
    private static final Logger logger = Logger.getLogger(ErrorDialog.class);
    /**
     * The maximum width that we allow message dialogs to have.
     */
    private static final int MAX_MSG_PANE_WIDTH = 340;

    // private StyledHTMLEditorPane htmlMsgEditorPane = new StyledHTMLEditorPane();
    /**
     * The maximum height that we allow message dialogs to have.
     */
    private static final int MAX_MSG_PANE_HEIGHT = 800;

    // private JScrollPane stackTraceScrollPane = new JScrollPane();

    // private TransparentPanel buttonsPanel = new TransparentPanel(new FlowLayout(FlowLayout
    // .CENTER));
    /**
     * Load the "net.java.sip.communicator.SHOW_STACK_TRACE" property to
     * determine whether we should show stack trace in error dialogs.
     * Default is show. cmeng: set to false as cannot support TransparentPanel
     */
    private static String showStackTraceDefaultProp = aTalkApp.getResString(R.string.service_gui_SHOW_STACK_TRACE);
    /**
     * Should we show stack trace.
     */
    private final static boolean showStackTrace
            = (showStackTraceDefaultProp != null) && Boolean.parseBoolean(showStackTraceDefaultProp);
    private JButton okButton = new JButton(aTalkApp.getResString(R.string.service_gui_OK));
    private JLabel iconLabel = new JLabel(new ImageIcon(DesktopUtilActivator.getImage("service.gui.icons.ERROR_ICON")));
    private JTextArea stackTraceTextArea = new JTextArea();
    private TransparentPanel infoMessagePanel = new TransparentPanel();
    private TransparentPanel messagePanel = new TransparentPanel(new BorderLayout());
    private TransparentPanel mainPanel = new TransparentPanel(new BorderLayout(10, 10));
    /**
     * The indicator which determines whether the details of the error are
     * currently shown.
     * <p>
     * The indicator is initially set to <tt>true</tt> because the constructor
     * {@link #ErrorDialog(Frame, String, String, Throwable)} calls
     * {@link #showOrHideDetails()} and thus <tt>ErrorDialog</tt> defaults to
     * not showing the details of the error.
     * </p>
     */
    private boolean detailsShown = true;
    /**
     * The type of this <tt>ErrorDialog</tt> (e.g. {@link #WARNING}). The
     * default <tt>ErrorDialog</tt> displays an error.
     */
    private int type = 0;

    /**
     * Initializes a new <tt>ErrorDialog</tt> with a specific owner
     * <tt>Frame</tt>, title and message to be displayed.
     *
     * @param owner the dialog owner
     * @param title the title of the dialog
     * @param message the message to be displayed
     */
    public ErrorDialog(Frame owner, String title, String message)
    {

        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        if (showStackTrace) {
            // this.stackTraceScrollPane.setBorder(BorderFactory.createLineBorder(iconLabel
            // .getForeground()));
            // this.stackTraceScrollPane.setHorizontalScrollBarPolicy(JScrollPane
            // .HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        this.setTitle(title);
        this.init();
    }

    /**
     * Initializes a new <tt>ErrorDialog</tt> with a specific owner
     * <tt>Frame</tt>, title, error message to be displayed and the
     * <tt>Throwable</tt> associated with the error.
     *
     * @param owner the dialog owner
     * @param title the title of the dialog
     * @param message the message to be displayed
     * @param e the exception corresponding to the error
     */
    public ErrorDialog(Frame owner, String title, String message, Throwable e)
    {
        this(owner, title, message);
        if (showStackTrace && e != null) {
            this.setTitle(title);
            showOrHideDetails();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();

            String stackTrace = sw.toString();

            try {
                sw.close();
            } catch (IOException ex) {
                //really shouldn't happen. but log anyway
                logger.error("Failed to close a StringWriter. ", ex);
            }
        }
    }

    /**
     * Initializes a new <tt>ErrorDialog</tt> with a specific owner
     * <tt>Frame</tt>, title and message to be displayed and of a specific type.
     *
     * @param owner the dialog owner
     * @param title the title of the error dialog
     * @param message the message to be displayed
     * @param type the dialog type
     */
    public ErrorDialog(Frame owner, String title, String message, int type)
    {
        this(owner, title, message);
        if (type == WARNING) {
            this.type = type;
        }
    }

    /**
     * Initializes this dialog.
     */
    private void init()
    {
        // this.getRootPane().setDefaultButton(okButton);

        // this.stackTraceScrollPane.getViewport().add(stackTraceTextArea);
        // this.stackTraceScrollPane.setPreferredSize(new Dimension(this.getWidth(), 100));

        // this.buttonsPanel.add(okButton);

        // this.okButton.addActionListener(this);

        this.mainPanel.add(iconLabel, BorderLayout.WEST);
        this.messagePanel.add(infoMessagePanel, BorderLayout.NORTH);
        this.mainPanel.add(messagePanel, BorderLayout.CENTER);
        // this.mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
    }

    /**
     * Shows if previously hidden or hides if previously shown the details of
     * the error. Called when the "more" link is clicked.
     */
    public void showOrHideDetails()
    {
        String startDivTag = "<div id=\"message\">";
        String endDivTag = "</div>";
        String msgString;

        detailsShown = !detailsShown;

        if (detailsShown) {
            msgString = startDivTag + " <p align=\"right\"><a href=\"\">&lt;&lt; Hide info</a></p>" + endDivTag;
        }
        else {
            msgString = startDivTag + " <p align=\"right\"><a href=\"\">More info &gt;&gt;</a></p>" + endDivTag;
        }
        this.pack();
    }

    /**
     * Shows the dialog.
     */
    public void showDialog()
    {
        this.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(screenSize.width / 2 - this.getWidth() / 2, screenSize.height / 2 - this.getHeight() / 2);
        this.setVisible(true);

    }

    /**
     * Handles the <tt>ActionEvent</tt>. Depending on the user choice sets
     * the return code to the appropriate value.
     *
     * @param e the <tt>ActionEvent</tt> instance that has just been fired.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton button = (JButton) e.getSource();

        if (button.equals(okButton))
            this.dispose();
    }

    /**
     * Close the ErrorDialog. This function is invoked when user
     * presses the Escape key.
     *
     * // @param isEscaped Specifies whether the close was triggered by pressing he escape key.
     */
    @Override
//    protected void close(boolean isEscaped)
//    {
//        this.okButton.doClick();
//    }

    /**
     * Update the ErrorDialog when the user clicks on the hyperlink.
     *
     * @param e The event generated by the click on the hyperlink.
     */
//    public void hyperlinkUpdate(HyperlinkEvent e)
//    {
//        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
//            showOrHideDetails();
//    }

    /**
     * Reloads icon.
     */
    public void loadSkin()
    {
        String icon = (type == WARNING) ? "service.gui.icons.WARNING_ICON" : "service.gui.icons.ERROR_ICON";

        // iconLabel.setIcon(new ImageIcon(DesktopUtilActivator.getImage(icon)));
    }
}
