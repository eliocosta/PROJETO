package br.edu.utfpr.emprego;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.text.ParseException;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import org.jfree.chart.ChartFactory;import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import br.gov.bcb.pec.sgs.casosdeuso.ws.comum.WSSerieVO;
import br.gov.bcb.pec.sgs.casosdeuso.ws.comum.WSValorSerieVO;
import br.gov.bcb.www3.wssgs.services.FachadaWSSGS.FachadaWSSGSProxy;

import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JFormattedTextField textDataInicial;
	private JFormattedTextField textDataFinal;
	private JPanel panelGrafico;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblDataInicial = new JLabel("Data Inicial");
		panel.add(lblDataInicial);
		
		MaskFormatter mascara = new MaskFormatter();
		try {
			mascara = new MaskFormatter("##/####");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mascara.setPlaceholderCharacter('_');
		
		textDataInicial = new JFormattedTextField(mascara);
		panel.add(textDataInicial);
		textDataInicial.setPreferredSize(new Dimension(52, 20));
		
		JLabel lblDataFinal = new JLabel("Data Final");
		panel.add(lblDataFinal);
		
		textDataFinal = new JFormattedTextField(mascara);
		panel.add(textDataFinal);
		textDataFinal.setPreferredSize(new Dimension(52, 20));
		
		JButton buttonGerarGrafico = new JButton("Gerar Gr\u00E1fico");
		buttonGerarGrafico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gerarGrafico();
			}
		});
		panel.add(buttonGerarGrafico);
		
		panelGrafico = new JPanel();
		contentPane.add(panelGrafico, BorderLayout.CENTER);
		panelGrafico.setLayout(new BorderLayout(0, 0));
	}

	public String getTextDataInicial() {
		return "01/"+textDataInicial.getText();
	}
	public String getTextDataFinal() {
		return "01/"+textDataFinal.getText();
	}
	
	private void gerarGrafico() {
		FachadaWSSGSProxy ws = new FachadaWSSGSProxy();
		
		try {
			WSSerieVO[] ret = ws.getValoresSeriesVO(new long[] {24372,24373,24375,24376}, this.getTextDataInicial(), this.getTextDataFinal());
			
			
//			24372	Empregados no setor público
//			24375	Empregados no setor privado - Com carteira
//			24376	Empregados no setor privado - Sem Carteira
//			24373	Empregado por conta própria
			
			TimeSeriesCollection dados = new TimeSeriesCollection();
			
			for (int i = 0; i < ret.length; i++) {
			
				TimeSeries serie = new TimeSeries(ret[i].getNomeAbreviado());
				
	 			for(WSValorSerieVO val : ret[i].getValores()) {
	 				Calendar cal = Calendar.getInstance();
	 				cal.set(Calendar.YEAR, val.getAno());
	 				cal.set(Calendar.MONTH, val.getMes());
	 				cal.set(Calendar.DAY_OF_MONTH, val.getDia());
	 				
	 				serie.add(new Month(cal.getTime()), val.getValor());
				}
	 			dados.addSeries(serie);
			}
 			
 			JFreeChart grafico = ChartFactory.createTimeSeriesChart("Empregos", "Mês", "Milhares", dados);
 			
 			ChartPanel panel = new ChartPanel(grafico);
 			
 			this.getPanelGrafico().removeAll();
 			this.getPanelGrafico().add(panel, BorderLayout.CENTER);
 			this.setSize((int)this.getSize().getWidth()+1, (int)this.getSize().getHeight()+1);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public JPanel getPanelGrafico() {
		return panelGrafico;
	}
}





