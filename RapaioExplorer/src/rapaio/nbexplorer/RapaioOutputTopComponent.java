/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rapaio.nbexplorer;

import java.awt.Dimension;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import rapaio.nbexplorer.Bundle;
import rapaio.nbexplorer.Bundle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//rapaio.nbexplorer//RapaioOutput//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "RapaioOutputTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "rapaio.nbexplorer.RapaioOutputTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RapaioOutputAction",
        preferredID = "RapaioOutputTopComponent")
@Messages({
    "CTL_RapaioOutputAction=RapaioOutput",
    "CTL_RapaioOutputTopComponent=RapaioOutput Window",
    "HINT_RapaioOutputTopComponent=This is Rapaio Output window"
})
public final class RapaioOutputTopComponent extends TopComponent {

    public RapaioOutputTopComponent() {
        initComponents();
        setName(Bundle.CTL_RapaioOutputTopComponent());
        setToolTipText(Bundle.HINT_RapaioOutputTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        oututTextPane = new javax.swing.JTextPane();

        setLayout(new java.awt.GridLayout(1, 0));

        oututTextPane.setFont(new java.awt.Font("Liberation Mono", 0, 12)); // NOI18N
        oututTextPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                oututTextPaneComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(oututTextPane);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void oututTextPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_oututTextPaneComponentResized
        Dimension dim = oututTextPane.getSize();
        int maxwidth = oututTextPane.getFontMetrics(oututTextPane.getFont()).getMaxAdvance();
        
        NbPreferences.forModule(NBOutputServer.class).putInt("textWidth", (int) (dim.getWidth() / maxwidth));

    }//GEN-LAST:event_oututTextPaneComponentResized
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane oututTextPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    public void appendString(String msg) {
        oututTextPane.setText(oututTextPane.getText() + msg + "\n");
    }

    @Override
    public Action[] getActions() {
        return new Action[] {new DefaultEditorKit.CopyAction()};
    }
    
    
}
