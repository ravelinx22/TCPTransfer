package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import TCPClient.TCPClient;

public class Interfaz extends JFrame implements ListSelectionListener, ActionListener{

	/* Constants */
	// 3MB
	private final static String LARGE_FILE = "LARGE_FILE";
	// 15MB
	private final static String MEDIUM_FILE = "MEDIUM_FILE";
	// 45MB
	private final static String SMALL_FILE = "SMALL_FILE";

	final static String DETENER = "DETENER";
	final static String DESCARGAR = "DESCARGAR";
	final static String CONECTAR = "CONECTAR";

	TCPClient fc;

	JList listaArchivos = new JList<>();
	JScrollPane scroll;

	JButton descargar;
	JButton detener;

	JRadioButton largeFileSelector;
	JRadioButton mediumFileSelector;
	JRadioButton smallFileSelector;

	JButton conectar;

	JTextField estado;



	public Interfaz() {
		setLocationRelativeTo(null);

		setTitle( "TCP Cliente" );
		setSize( 780, 300 );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setResizable(false);
		setLayout(new BorderLayout());


		JPanel panelConexion = new JPanel();
		panelConexion.setBorder(new TitledBorder("Conexion"));
		panelConexion.setLayout(new GridLayout(1, 7));

		JTextField host = new JTextField("localhost");
		JTextField puerto = new JTextField(""+1988);
		estado = new JTextField("Desconectado");
		conectar = new JButton("Conectar");
		conectar.addActionListener(this);
		conectar.setActionCommand(CONECTAR);


		host.setEditable(false);
		puerto.setEditable(false);
		estado.setEditable(false);

		panelConexion.add(new JLabel("Host:"));
		panelConexion.add(host);
		panelConexion.add(new JLabel("Puerto:"));
		panelConexion.add(puerto);
		panelConexion.add(new JLabel("Estado:"));
		panelConexion.add(estado);
		panelConexion.add(conectar);

		JPanel panelArchivos = new JPanel();
		panelArchivos.setBorder(new TitledBorder("Seleccionar archivo:"));
		panelArchivos.setLayout(new GridLayout(4, 1));

		largeFileSelector = new JRadioButton("Large File");
		largeFileSelector.setActionCommand(LARGE_FILE);
		largeFileSelector.addActionListener(this);
		mediumFileSelector = new JRadioButton("Medium File");
		mediumFileSelector.setActionCommand(MEDIUM_FILE);
		mediumFileSelector.addActionListener(this);
		smallFileSelector = new JRadioButton("Small File");
		smallFileSelector.setActionCommand(SMALL_FILE);
		smallFileSelector.addActionListener(this);

		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(1, 2));
		descargar = new JButton("Empezar descarga");
		descargar.setActionCommand(DESCARGAR);
		descargar.addActionListener(this);
		detener = new JButton("Detener descarga");
		detener.setActionCommand(DETENER);
		detener.addActionListener(this);


		panelArchivos.add(largeFileSelector);
		panelArchivos.add(mediumFileSelector);
		panelArchivos.add(smallFileSelector);
		panelBotones.add(descargar);
		panelBotones.add(detener);
		panelArchivos.add(panelBotones);


		JPanel panelDescargas = new JPanel();
		listaArchivos = new JList<>();
		listaArchivos.addListSelectionListener(this);
		scroll = new JScrollPane( listaArchivos );
		scroll.setPreferredSize( new Dimension( 250, 185 ) );
		panelDescargas.setBorder(new TitledBorder("Decargas"));
		panelDescargas.add( scroll, BorderLayout.CENTER );

		add(panelArchivos, BorderLayout.CENTER);		
		add(panelConexion, BorderLayout.NORTH);
		add(panelDescargas, BorderLayout.EAST);

		refrescar();
		setVisible(true);	
	}	

	public void iniciarDescarga() throws IOException {
		class Ayuda extends Thread {
			public void run()  {
				try {
					if(largeFileSelector.isSelected())
						fc.sendFileSize(LARGE_FILE);
					else if(mediumFileSelector.isSelected())
						fc.sendFileSize(MEDIUM_FILE);
					else if(smallFileSelector.isSelected())
						fc.sendFileSize(SMALL_FILE);

					refrescar();
					estado.setText("Desconectado");
				}
				catch(Exception e){}
			}
		}

		(new Ayuda()).start();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals(CONECTAR))
		{
			if(conectar.getText().equals("Conectar")) {
				estado.setText("Conectado");
				fc = new TCPClient("localhost", 1988);
				conectar.setText("Desconectar");
			}
			else {
				estado.setText("Desconectado");
				fc.stopConnection();
				fc = null;
				conectar.setText("Conectar");
			}
		}
		else if(arg0.getActionCommand().equals(DESCARGAR))
			try {
				iniciarDescarga();

			} catch (IOException e) {
				e.printStackTrace();
			}
		else if(arg0.getActionCommand().equals(DETENER)) {
			fc.stopConnection();

		}

		else if(arg0.getActionCommand().equals(LARGE_FILE)) {
			mediumFileSelector.setSelected(false);
			smallFileSelector.setSelected(false);
		}
		else if(arg0.getActionCommand().equals(MEDIUM_FILE)) {
			largeFileSelector.setSelected(false);
			smallFileSelector.setSelected(false);
		}
		else if(arg0.getActionCommand().equals(SMALL_FILE)) {
			largeFileSelector.setSelected(false);
			mediumFileSelector.setSelected(false);
		}
	}

	public void refrescar( )
	{
		ArrayList<String> archivos = new ArrayList( );

		// Saca la lista de archivos de directorio
		File directorio = new File( "./data" );
		File[] elementos = directorio.listFiles( );
		if( elementos != null )
		{
			for( int i = 0; i < elementos.length; i++ )
			{
				// Verifica si es directorio o si es archivo

				if( elementos[ i ].isFile( ) )
				{
					int k = elementos[ i ].getAbsolutePath( ).lastIndexOf(File.separator);
					archivos.add( elementos[ i ].getAbsolutePath( ).substring(k+1));
				}
			}
		}

		listaArchivos.setListData( archivos.toArray() );
	}

	public static void main(String[] args) {
		new Interfaz();
	}

}
