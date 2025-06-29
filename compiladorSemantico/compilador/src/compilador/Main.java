package compilador;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;

import lexicoSintaticoSemantico.*;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextArea textAreaCodigo;
	private JTextArea lineNumbers;
	private JTextArea messageArea;
	private JTextArea retornoCodigo;

	public File currentFile;

	private boolean ctrlPressed = false;
	private JPanel messagePanel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JButton criarBotao(String texto) {
		JButton botao = new JButton(texto);
		botao.setVerticalTextPosition(SwingConstants.BOTTOM);
		botao.setPreferredSize(new Dimension(100, 60));
		botao.setFont(new Font("SansSerif", Font.BOLD, 10));
		botao.setBackground(new Color(220, 220, 220));
		botao.setForeground(Color.DARK_GRAY);
		botao.setFocusPainted(false);
		botao.setBorder(new BordaLevementeArredondada(10));
		botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
		botao.setVerticalAlignment(SwingConstants.BOTTOM);
		botao.setHorizontalTextPosition(SwingConstants.CENTER);
		return botao;
	}

	static class BordaLevementeArredondada extends javax.swing.border.AbstractBorder {
		private final int raio;

		public BordaLevementeArredondada(int raio) {
			this.raio = raio;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.GRAY);
			g2.drawRoundRect(x, y, width - 1, height - 1, raio, raio);
		}
	}

	public Main() {
		setResizable(false);
		setPreferredSize(new Dimension(1500, 800));
		setMinimumSize(new Dimension(150, 80));
		getContentPane().setPreferredSize(new Dimension(1500, 800));
		getContentPane().setMinimumSize(new Dimension(1500, 800));
		getContentPane().setMaximumSize(new Dimension(1500, 800));
		setMaximumSize(new Dimension(1500, 800));
		setTitle("Compiladores - Grupo 6");
		setSize(1500, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());

		JPanel headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
		headerPanel.setBackground(Color.LIGHT_GRAY);
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

		JPanel statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(1500, 25));
		statusPanel.setBounds(new Rectangle(1500, 25, 1500, 25));
		statusPanel.setMinimumSize(new Dimension(1500, 0));
		statusPanel.setMaximumSize(new Dimension(1500, 25));
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(null);

		JLabel statusLabel = new JLabel("Arquivo: (Nenhum)");
		statusLabel.setBounds(0, 0, 1490, 25);
		statusLabel.setMinimumSize(new Dimension(1500, 25));
		statusLabel.setMaximumSize(new Dimension(1500, 25));
		statusLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusLabel.setFont(new Font("Dialog", Font.BOLD, 8));
		statusPanel.add(statusLabel);

		setVisible(true);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel editorPanel = new JPanel();
		editorPanel.setPreferredSize(new Dimension(1500, 705));
		editorPanel.setLayout(new BorderLayout());

		textAreaCodigo = new JTextArea();
		textAreaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textAreaCodigo.setLineWrap(false);
		textAreaCodigo.setWrapStyleWord(true);
		textAreaCodigo.setBackground(Color.WHITE);
		textAreaCodigo.setForeground(Color.BLACK);

		lineNumbers = new JTextArea("1");
		lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
		lineNumbers.setEditable(false);
		lineNumbers.setBackground(Color.LIGHT_GRAY);
		lineNumbers.setForeground(Color.GRAY);

		JScrollPane scrollTextArea = new JScrollPane(textAreaCodigo);
		scrollTextArea.setPreferredSize(new Dimension(1500, 600));
		scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollTextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollPane scrollLineNumbers = new JScrollPane(lineNumbers);
		scrollLineNumbers.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollLineNumbers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		scrollLineNumbers.setPreferredSize(new Dimension(50, 600));

		scrollTextArea.getVerticalScrollBar().addAdjustmentListener(e -> {
			scrollLineNumbers.getVerticalScrollBar().setValue(e.getValue());
		});

		editorPanel.add(scrollLineNumbers, BorderLayout.WEST);
		editorPanel.add(scrollTextArea, BorderLayout.CENTER);

		getContentPane().add(editorPanel, BorderLayout.CENTER);

		messagePanel = new JPanel();
		messagePanel.setPreferredSize(new Dimension(1500, 90));
		editorPanel.add(messagePanel, BorderLayout.SOUTH);
		messagePanel.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scrollTextArea_1 = new JScrollPane((Component) null);
		scrollTextArea_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollTextArea_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		messagePanel.add(scrollTextArea_1);

		retornoCodigo = new JTextArea();
		retornoCodigo.setEditable(false);
		retornoCodigo.setFocusable(false);
		scrollTextArea_1.setViewportView(retornoCodigo);
		retornoCodigo.setWrapStyleWord(true);
		retornoCodigo.setLineWrap(false);
		retornoCodigo.setForeground(Color.BLACK);
		retornoCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));
		retornoCodigo.setBackground(Color.WHITE);

		textAreaCodigo.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLineNumbers();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLineNumbers();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLineNumbers();
			}
		});

		setVisible(true);

		JButton btnNew = criarBotao("Novo (Ctrl + N)");
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaCodigo.setText("");
				retornoCodigo.setText("");
				statusLabel.setText("Arquivo: Nenhum");

				Main.this.currentFile = null;
				updateLineNumbers();
			}
		});

		InputMap inputMap = btnNew.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = btnNew.getActionMap();

		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
		String actionKey = "novoArquivo";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnNew.doClick();
			}
		});

		JButton btnOpen = criarBotao("Abrir (Ctrl + O)");
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(btnOpen) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						textAreaCodigo.read(reader, null);
						updateLineNumbers();
						textAreaCodigo.getDocument().addDocumentListener(new DocumentListener() {
							@Override
							public void insertUpdate(DocumentEvent e) {
								updateLineNumbers();
							}

							@Override
							public void removeUpdate(DocumentEvent e) {
								updateLineNumbers();
							}

							@Override
							public void changedUpdate(DocumentEvent e) {
								updateLineNumbers();
							}
						});
						retornoCodigo.setText("");
						statusLabel.setText("Arquivo: " + file.getAbsolutePath());

						Main.this.currentFile = file;
						JOptionPane.showMessageDialog(btnOpen, "Arquivo aberto com sucesso!");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(btnOpen, "Erro ao abrir arquivo", "Erro",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		inputMap = btnOpen.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnOpen.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
		actionKey = "abrirArquivo";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOpen.doClick();
			}
		});

		JButton btnSave = criarBotao("Salvar (Ctrl + S)");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Main.this.currentFile == null) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Salvar Arquivo");

					int userSelection = fileChooser.showSaveDialog(null);

					if (userSelection == JFileChooser.APPROVE_OPTION) {
						File fileToSave = fileChooser.getSelectedFile();

						if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
							fileToSave = new File(fileToSave + ".txt");
						}

						try (FileWriter writer = new FileWriter(fileToSave)) {
							writer.write(textAreaCodigo.getText());
							// arquivo.setText(fileToSave.getPath()); // Remove or update this line if not
							// needed
							Main.this.currentFile = fileToSave;
							JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!");
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo: " + ex.getMessage());
						}
					}
				} else {
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.this.currentFile))) {
						writer.write(textAreaCodigo.getText());
						JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!");
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo: " + ex.getMessage());
					}
				}
			}
		});

		inputMap = btnSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnSave.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
		actionKey = "salvarArquivo";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSave.doClick();
			}
		});

		JButton btnCopy = criarBotao("Copiar (Ctrl + C)");
		btnCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaCodigo.copy();
			}
		});

		inputMap = btnCopy.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnCopy.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		actionKey = "copiarTexto";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCopy.doClick();
			}
		});

		JButton btnPaste = criarBotao("Colar (Ctrl + V)");
		btnPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaCodigo.paste();
			}
		});

		inputMap = btnPaste.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnPaste.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
		actionKey = "colarTexto";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPaste.doClick();
			}
		});

		JButton btnCut = criarBotao("Recortar (Ctrl + X)");
		btnCut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textAreaCodigo.cut();
			}
		});

		inputMap = btnCut.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnCut.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
		actionKey = "cortarTexto";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCut.doClick();
			}
		});
		JButton btnCompile = criarBotao("Compilar (F7)");
		btnCompile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String codigoFonte = textAreaCodigo.getText();

				Lexico lexico = new Lexico(codigoFonte);
				Sintatico sintatico = new Sintatico();
				Semantico semantico = new Semantico();

				StringBuilder resultado = new StringBuilder();

				String tokenErro = "";
				try {
					Token token = null;

					while ((token = lexico.nextToken()) != null) {
						// resultado.append(String.format(
							// "%-7d%-20s%-10s\n",
							// getLinhaPorPosicao(textAreaCodigo.getText(), token.getPosition()),
							// getClassName(token.getId()),
							// token.getLexeme()));
							
							sintatico.parse(lexico, semantico);
							tokenErro = getClassName(token.getId());
					}

					retornoCodigo.setText(resultado.toString());
					retornoCodigo.append("Programa compilado com sucesso!");

					String fileName;
					if (currentFile != null) {
						fileName = currentFile.getPath().replace(".txt", "") + ".il";
					} else {
						JOptionPane.showMessageDialog(null,
								"Arquivo não foi salvo anteriormente. Salve o arquivo antes de compilar.", "Erro",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					try (FileWriter fileWriter = new FileWriter(fileName)) {
						// fileWriter.write(semantico.codigo_objeto);
						fileWriter.write("TESTESTET");
						retornoCodigo.append("Arquivo salvo como " + fileName);
					} catch (IOException ioException) {
						retornoCodigo.append("Erro ao salvar o arquivo: " + ioException.getMessage());
					}

				} catch (LexicalError error) {
					int linha = getLinhaPorPosicao(textAreaCodigo.getText(), error.getPosition());
					// String lexema = error.getLexema();

					resultado.setLength(0);
					resultado.append("ERRO na linha " + linha + " - " + error.getMessage());
					retornoCodigo.setText(resultado.toString());
					// return;

				} catch (SyntaticError error) {
					int linha = getLinhaPorPosicao(textAreaCodigo.getText(), error.getPosition());
					String mensagem = "ERRO na linha " + linha + " - " + "encontrado " + tokenErro + " "
							+ error.getMessage();

					resultado.setLength(0);
					resultado.append(mensagem);
					retornoCodigo.setText(resultado.toString());
					return;

				} catch (SemanticError error) {
					int linha = getLinhaPorPosicao(textAreaCodigo.getText(), error.getPosition());
					String mensagem = "ERRO na linha " + linha + " - " + error.getMessage();

					resultado.setLength(0);
					resultado.append(mensagem);
					retornoCodigo.setText(resultado.toString());
					return;
				}
			}
		});

		inputMap = btnCompile.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnCompile.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
		actionKey = "compilarArquivo";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnCompile.doClick();
			}
		});

		JButton btnTeam = criarBotao("Equipe (F1)");
		btnTeam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				retornoCodigo.setText(
						"Equipe de Desenvolvimento:\n- Guilherme Maba \n- Enzo Gabriel da Rocha \n- Igor Zafriel Schmidt");
			}
		});

		inputMap = btnTeam.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = btnTeam.getActionMap();

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		actionKey = "showTeam";

		inputMap.put(keyStroke, actionKey);
		actionMap.put(actionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnTeam.doClick();
			}
		});

		btnNew.setIcon(new ImageIcon(Main.class.getResource("/img/mais.png")));
		btnOpen.setIcon(new ImageIcon(Main.class.getResource("/img/pasta-aberta.png")));
		btnSave.setIcon(new ImageIcon(Main.class.getResource("/img/opcao-de-salvar-arquivo.png")));
		btnCopy.setIcon(new ImageIcon(Main.class.getResource("/img/copiando.png")));
		btnPaste.setIcon(new ImageIcon(Main.class.getResource("/img/colar.png")));
		btnCut.setIcon(new ImageIcon(Main.class.getResource("/img/corte.png")));
		btnCompile.setIcon(new ImageIcon(Main.class.getResource("/img/compilar.png")));
		btnTeam.setIcon(new ImageIcon(Main.class.getResource("/img/trabalho-em-equipe.png")));

		headerPanel.add(btnNew);
		headerPanel.add(btnOpen);
		headerPanel.add(btnSave);
		headerPanel.add(btnCopy);
		headerPanel.add(btnPaste);
		headerPanel.add(btnCut);
		headerPanel.add(btnCompile);
		headerPanel.add(btnTeam);

		getContentPane().add(headerPanel, BorderLayout.NORTH);

		JSplitPane splitPane_1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPanel, messagePanel);
		getContentPane().add(splitPane_1, BorderLayout.CENTER);
		splitPane_1.setDividerLocation(550);

	}

	private void updateLineNumbers() {
		int lineCount = textAreaCodigo.getLineCount();
		StringBuilder lines = new StringBuilder();

		for (int i = 1; i <= lineCount; i++) {
			lines.append(i).append("\n");
		}

		lineNumbers.setText(lines.toString());
	}

	public static String getClassName(int token) {
		switch (token) {
			case Constants.t_identificador:
				return "identificador";
			case Constants.t_cte_int:
				return "constante_int";
			case Constants.t_cte_float:
				return "constante_float";
			case Constants.t_cte_char:
				return "constante_char";
			case Constants.t_cte_string:
				return "constante_string";
			case Constants.t_pr_bool:
				return "palavra reservada";
			case Constants.t_pr_case:
				return "palavra reservada";
			case Constants.t_pr_char:
				return "palavra reservada";
			case Constants.t_pr_echo:
				return "palavra reservada";
			case Constants.t_pr_do:
				return "palavra reservada";
			case Constants.t_pr_end:
				return "palavra reservada";
			case Constants.t_pr_false:
				return "palavra reservada";
			case Constants.t_pr_float:
				return "palavra reservada";
			case Constants.t_pr_int:
				return "palavra reservada";
			case Constants.t_pr_local:
				return "palavra reservada";
			case Constants.t_pr_module:
				return "palavra reservada";
			case Constants.t_pr_request:
				return "palavra reservada";
			case Constants.t_pr_string:
				return "palavra reservada";
			case Constants.t_pr_switch:
				return "palavra reservada";
			case Constants.t_pr_true:
				return "palavra reservada";
			case Constants.t_pr_until:
				return "palavra reservada";
			case Constants.t_pr_while:
				return "palavra reservada";
			case Constants.t_TOKEN_24:
				return "símbolo especial";
			case Constants.t_TOKEN_25:
				return "símbolo especial";
			case Constants.t_TOKEN_26:
				return "símbolo especial";
			case Constants.t_TOKEN_27:
				return "símbolo especial";
			case Constants.t_TOKEN_28:
				return "símbolo especial";
			case Constants.t_TOKEN_29:
				return "símbolo especial";
			case Constants.t_TOKEN_30:
				return "símbolo especial";
			case Constants.t_TOKEN_31:
				return "símbolo especial";
			case Constants.t_TOKEN_32:
				return "símbolo especial";
			case Constants.t_TOKEN_33:
				return "símbolo especial";
			case Constants.t_TOKEN_34:
				return "símbolo especial";
			case Constants.t_TOKEN_35:
				return "símbolo especial";
			case Constants.t_TOKEN_36:
				return "símbolo especial";
			case Constants.t_TOKEN_37:
				return "símbolo especial";
			case Constants.t_TOKEN_38:
				return "símbolo especial";
			case Constants.t_TOKEN_39:
				return "símbolo especial";

			default:
				return "desconhecido";
		}
	}

	public static int getLinhaPorPosicao(String texto, int posicao) {
		int linha = 1;
		for (int i = 0; i < posicao && i < texto.length(); i++) {
			if (texto.charAt(i) == '\n') {
				linha++;
			}
		}
		return linha;
	}
}
