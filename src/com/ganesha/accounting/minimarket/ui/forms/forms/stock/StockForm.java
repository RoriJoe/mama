package com.ganesha.accounting.minimarket.ui.forms.forms.stock;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.util.DBUtils;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;

public class StockForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtSatuan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblKodeTerakhirValue;
	private XJLabel lblBarcode;
	private XJTextField txtBarcode;
	private XJLabel lblKodeTerakhir;
	private ActionType actionType;
	private JSeparator separator;
	private JPanel pnlKanan;
	private XJLabel lblJumlahSaatIni;
	private XJTextField txtJumlahSaatIni;
	private XJLabel lblHargaBeli;
	private XJTextField txtHargaBeli;
	private JPanel pnlKode;
	private XJLabel lblHpp;
	private XJTextField txtHpp;
	private XJLabel lblHargaJual;
	private XJTextField txtHargaJual;
	private XJButton btnBatal;
	private XJLabel lblStokMinimum;
	private XJTextField txtStokMinimum;
	private XJButton btnGenerateBarcode;
	private XJButton btnHapusBarang;
	private JPanel pnlDisable;
	private XJCheckBox chkDisabled;

	private boolean deleted;

	public StockForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setCloseOnEsc(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		setTitle("Form Barang");
		getContentPane().setLayout(
				new MigLayout("", "[400,grow][400]",
						"[grow][grow][grow][10][grow]"));

		pnlKode = new JPanel();
		pnlKode.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlKode, "cell 0 0 2 1,grow");
		pnlKode.setLayout(new MigLayout("", "[150][][100][]", "[][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKode.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		pnlKode.add(txtKode, "cell 1 0 2 1,growx");

		lblKodeTerakhir = new XJLabel();
		lblKodeTerakhir.setFont(new Font("Tahoma", Font.BOLD, 12));
		pnlKode.add(lblKodeTerakhir, "cell 1 1");
		lblKodeTerakhir.setText("Kode Terakhir:");

		lblKodeTerakhirValue = new XJLabel();
		lblKodeTerakhirValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		pnlKode.add(lblKodeTerakhirValue, "cell 2 1");
		lblKodeTerakhirValue.setText("");

		lblBarcode = new XJLabel();
		pnlKode.add(lblBarcode, "cell 0 2");
		lblBarcode.setText("Barcode");

		txtBarcode = new XJTextField();
		pnlKode.add(txtBarcode, "cell 1 2 2 1,growx");
		txtBarcode.setEditable(false);

		btnGenerateBarcode = new XJButton();
		btnGenerateBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setBarcode(System.currentTimeMillis());
			}
		});
		btnGenerateBarcode.setText("Generate Barcode");
		pnlKode.add(btnGenerateBarcode, "cell 3 2");

		JPanel pnlKiri = new JPanel();
		pnlKiri.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlKiri, "cell 0 1,grow");
		pnlKiri.setLayout(new MigLayout("", "[150][grow]", "[][][][]"));

		XJLabel lblNama = new XJLabel();
		pnlKiri.add(lblNama, "cell 0 0");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlKiri.add(txtNama, "cell 1 0,growx");

		XJLabel lblSatuan = new XJLabel();
		pnlKiri.add(lblSatuan, "cell 0 1");
		lblSatuan.setText("Satuan");

		txtSatuan = new XJTextField();
		pnlKiri.add(txtSatuan, "cell 1 1,growx");

		lblJumlahSaatIni = new XJLabel();
		pnlKiri.add(lblJumlahSaatIni, "cell 0 2");
		lblJumlahSaatIni.setText("Jumlah Saat Ini");

		txtJumlahSaatIni = new XJTextField();
		txtJumlahSaatIni.setEditable(false);
		pnlKiri.add(txtJumlahSaatIni, "cell 1 2,growx");

		lblStokMinimum = new XJLabel();
		lblStokMinimum.setText("Stok Minimum");
		pnlKiri.add(lblStokMinimum, "cell 0 3");

		txtStokMinimum = new XJTextField();
		txtStokMinimum
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtStokMinimum.setText("0");
		pnlKiri.add(txtStokMinimum, "cell 1 3,growx");

		pnlKanan = new JPanel();
		pnlKanan.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlKanan, "cell 1 1,grow");
		pnlKanan.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		lblHargaBeli = new XJLabel();
		lblHargaBeli.setText("Harga Beli");
		pnlKanan.add(lblHargaBeli, "cell 0 0");

		txtHargaBeli = new XJTextField();
		txtHargaBeli.setEditable(false);
		pnlKanan.add(txtHargaBeli, "cell 1 0,growx");

		lblHpp = new XJLabel();
		lblHpp.setText("HPP");
		pnlKanan.add(lblHpp, "cell 0 1");

		txtHpp = new XJTextField();
		txtHpp.setEditable(false);
		pnlKanan.add(txtHpp, "cell 1 1,growx");

		lblHargaJual = new XJLabel();
		lblHargaJual.setText("Harga Jual");
		pnlKanan.add(lblHargaJual, "cell 0 2");

		txtHargaJual = new XJTextField();
		txtHargaJual
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtHargaJual.setText("0");
		pnlKanan.add(txtHargaJual, "cell 1 2,growx");

		pnlDisable = new JPanel();
		getContentPane().add(pnlDisable, "cell 0 2 2 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, 12));
		chkDisabled.setText("Item ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 3 2 1,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 4 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[grow]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");

		btnHapusBarang = new XJButton();
		btnHapusBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					deleted = true;
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		btnHapusBarang
				.setText("<html><center>Hapus<br/>Barang</center></html>");
		pnlButton.add(btnHapusBarang, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(null);
	}

	public String getKodeBarang() {
		return txtKode.getText();
	}

	public void setFormDetailValue(ItemStock itemStock) {
		Item item = itemStock.getItem();
		txtKode.setText(item.getCode());
		txtNama.setText(item.getName());
		txtBarcode.setText(item.getBarcode());
		txtSatuan.setText(itemStock.getUnit());
		txtJumlahSaatIni.setText(Formatter.formatNumberToString(itemStock
				.getStock()));
		txtStokMinimum.setText(Formatter.formatNumberToString(itemStock
				.getMinimumStock()));
		txtHargaBeli.setText(Formatter.formatNumberToString(itemStock
				.getBuyPrice()));
		txtHpp.setText(Formatter.formatNumberToString(itemStock.getHpp()));
		txtHargaJual.setText(Formatter.formatNumberToString(itemStock
				.getSellPrice()));
		chkDisabled.setSelected(itemStock.getDisabled());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void initForm() throws ActionTypeNotSupported {
		String kodeTerakhir = DBUtils.getInstance().getLastValue("items",
				"code", String.class);
		lblKodeTerakhirValue.setText(String.valueOf(kodeTerakhir));

		if (actionType == ActionType.CREATE) {
			txtJumlahSaatIni.setText("0");
			txtHargaBeli.setText("0");
			txtHpp.setText("0");
			txtHargaJual.setText("0");
			btnGenerateBarcode.setVisible(true);
		} else if (actionType == ActionType.UPDATE) {
			lblKodeTerakhir.setVisible(false);
			lblKodeTerakhirValue.setVisible(false);
			txtKode.setEditable(false);
			txtSatuan.setEditable(false);
			if (txtBarcode.getText().trim().equals("")) {
				btnGenerateBarcode.setVisible(true);
			} else {
				btnGenerateBarcode.setVisible(false);
			}
		} else if (actionType == ActionType.READ) {
			lblKodeTerakhir.setVisible(false);
			lblKodeTerakhirValue.setVisible(false);
			txtKode.setEditable(false);
			txtNama.setEditable(false);
			txtSatuan.setEditable(false);
			txtStokMinimum.setEditable(false);
			txtHargaJual.setEditable(false);
			btnSimpan.setEnabled(false);
			btnGenerateBarcode.setVisible(false);
		} else {
			throw new ActionTypeNotSupported(actionType);
		}
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			StockFacade facade = StockFacade.getInstance();

			String code = txtKode.getText();
			String name = txtNama.getText();
			String barcode = txtBarcode.getText();
			String unit = txtSatuan.getText();
			BigDecimal buyPrice = BigDecimal
					.valueOf(Formatter.formatStringToNumber(
							txtHargaBeli.getText()).doubleValue());
			BigDecimal hpp = BigDecimal.valueOf(Formatter.formatStringToNumber(
					txtHpp.getText()).doubleValue());
			BigDecimal sellPrice = BigDecimal
					.valueOf(Formatter.formatStringToNumber(
							txtHargaJual.getText()).doubleValue());
			int minimumStock = Formatter.formatStringToNumber(
					txtStokMinimum.getText()).intValue();

			boolean disabled = chkDisabled.isSelected();

			if (actionType == ActionType.CREATE) {
				facade.addNewItem(code, name, barcode, unit, buyPrice, hpp,
						sellPrice, minimumStock, disabled, deleted, session);
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				facade.updateExistingItem(code, name, barcode, unit, buyPrice,
						hpp, sellPrice, minimumStock, disabled, deleted,
						session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setBarcode(Object barcode) {
		txtBarcode.setText(barcode.toString());
	}

	private void validateForm() throws UserException {
		if (txtKode.getText().trim().equals("")) {
			throw new UserException("Kode Barang harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Barang harus diisi");
		}

		if (txtSatuan.getText().trim().equals("")) {
			throw new UserException("Satuan harus diisi");
		}
	}
}
