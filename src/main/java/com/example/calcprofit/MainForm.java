package com.example.calcprofit;

import com.example.calcprofit.dto.ResponseCurrency;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class MainForm extends JFrame {

    private final String API_KEY = "9ed62f19748831f2b2f51678cdbe3e95";
    private final String BASE = "USD";
    private final String CURRENCY_LIST = "USD,RUB";
    private final String TEMPLATE_GET_NOW = "http://data.fixer.io/api/latest?access_key=%s&symbols=%s&format=1";
    private final String TEMPLATE_GET_DATE = "http://data.fixer.io/api/%s?access_key=%s&symbols=%s&format=1";
    private final String USD = "USD";
    private final BigDecimal SPREAD = new BigDecimal("0.005").divide(new BigDecimal(2L));
    private final BigDecimal HALF_SPREAD = SPREAD.divide(new BigDecimal(2L));

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField tfAmount;
    private JButton bCalc;
    private JTextField tfprofit;
    private JPanel mainView;
    private JFormattedTextField ftfDate ; //= new JFormattedTextField(format);

    public MainForm(){



        bCalc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Date date;
                BigDecimal amount;
                try {
                    date = format.parse(ftfDate.getText());
                    amount = new BigDecimal(tfAmount.getText());
                } catch (ParseException e1) {
                    e1.printStackTrace();
                    date = new Date();
                    amount = BigDecimal.ONE;
                }

                RestTemplate restTemplate = new RestTemplate();
                Date finalDate = date;
                BigDecimal finalAmount = amount;
                Arrays.stream(CURRENCY_LIST.split(",")).forEach(currency ->
                {
                    ResponseCurrency responseCurrencyNow =
                            restTemplate.getForObject(
                                    String.format(TEMPLATE_GET_NOW, API_KEY, CURRENCY_LIST),
                                    ResponseCurrency.class);
                    ResponseCurrency responseCurrencyDate =
                            restTemplate.getForObject(
                                    String.format(TEMPLATE_GET_DATE, format.format(finalDate), API_KEY, CURRENCY_LIST),
                                    ResponseCurrency.class);

                    BigDecimal rateUSDToDate = responseCurrencyDate.getRates().get(USD);
                    rateUSDToDate = rateUSDToDate.add(HALF_SPREAD); // покупают + процент пол SPREAD
                    BigDecimal spentEURToDate = finalAmount.multiply(rateUSDToDate);

                    BigDecimal rateUSDNow = responseCurrencyNow.getRates().get(USD);
                    rateUSDNow = rateUSDNow.add(HALF_SPREAD); // покупают + процент пол SPREAD
                    BigDecimal amountNow = spentEURToDate.divide(rateUSDNow, RoundingMode.HALF_UP);

                    BigDecimal profit = amountNow.subtract(finalAmount).divide(finalAmount);

                    tfprofit.setText(profit.toString() + "%");

                });

            }
        });
    }



    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MainForm.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            MainForm ex = ctx.getBean(MainForm.class);
            ex.setContentPane(new MainForm().mainView);
            ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ex.pack();
            ex.setVisible(true);
        });

    }

}

