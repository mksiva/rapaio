/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rapaio.nbexplorer;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
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
        dtd = "-//rapaio.nbexplorer//EduGuessGraphic//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "RapaioGraphicTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "rapaio.nbexplorer.RapaioGraphicTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RapaioGraphicAction",
        preferredID = "RapaioGraphicTopComponent")
@Messages({
    "CTL_RapaioGraphicAction=RapaioGraphic",
    "CTL_RapaioGraphicTopComponent=RapaioGraphic Window",
    "HINT_RapaioGraphicTopComponent=This is Rapaio Graphic window"
})
public final class RapaioGraphicTopComponent extends TopComponent {

    private FigurePanel figurePanel = new FigurePanel();

    public RapaioGraphicTopComponent() {
        initComponents();
        setName(Bundle.CTL_RapaioGraphicTopComponent());
        setToolTipText(Bundle.HINT_RapaioGraphicTopComponent());
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);

        add(figurePanel);
    }

    public void setCurrentImage(BufferedImage image) {
        figurePanel.setCurrentImage(image);
        figurePanel.invalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.GridLayout(1, 0));
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        
        NbPreferences.forModule(NBOutputServer.class).putInt("graphicWidth", figurePanel.getWidth());
        NbPreferences.forModule(NBOutputServer.class).putInt("graphicHeight", figurePanel.getHeight());

        
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    }

    void readProperties(java.util.Properties p) {
    }
}
class FigurePanel extends JPanel {

    protected volatile BufferedImage currentImage;

    public void setCurrentImage(BufferedImage image) {
        currentImage = image;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final String drawingMessage = "No graphics to show ...";
        FontMetrics fm = g.getFontMetrics();

        if (currentImage != null) {
            if (currentImage.getWidth() != getWidth() || currentImage.getHeight() != getHeight()) {
                g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.drawImage(currentImage, 0, 0, null);
            }
        } else if (currentImage == null) {
            g.drawString(drawingMessage, getWidth() / 2 - fm.stringWidth(drawingMessage) / 2, getHeight() / 2 - fm.getHeight() / 2);
        }
    }
}
