package org.dalton;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("javadoc")
class ApplicationFrame extends Frame {
	private static final long serialVersionUID = 1L;
	public static final int FRAMESIZE = 600;

	public ApplicationFrame() { this("ApplicationFrame v1.0"); }

	public ApplicationFrame(String title) {
		super(title);
		createUI();
		this.setVisible(true);

		//this doesn't work after jar--JUnique is terrible, find a better solution.
//		//code to close old windows:
//		final String appId = "ApplicationFrame";
//		JUnique.sendMessage(appId, "close frame");
//		JUnique.releaseLock(appId);
//		
//		try {
//			Thread.sleep(2000); // JUnique takes forever to release the lock 
//								// and is not threadsafe and has no synchronization hooks
//								// VERY TERRIBLE!
//			JUnique.acquireLock(appId, new MessageHandler() {
//				public String handle(String message) {
//					if(message.contains("close frame")) {
//						dispose();
//						System.err.println("closed old frame");
//						return "successfully closed frame";
//					}
//					else return "a mysterious message: " + message;
//				}
//			});
//		} catch (AlreadyLockedException e) {
//			System.out.println("should never be here");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	protected void createUI()
	{
		setSize(FRAMESIZE, FRAMESIZE);
		center();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);

			}
		});
	}

	public void center()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		int x = (screenSize.width - frameSize.width)/ 2;
		int y = (screenSize.height - frameSize.height) / 2;
		setLocation(x,y);
	}
}

