package com.inv.ui.ctrl.views.home;

import com.inv.data.access.emums.OrderStatus;
import com.inv.ui.ctrl.views.orders.Orders;
import com.inv.ui.in.IWidget;
import com.inv.ui.util.Anima;
import com.inv.ui.util.BgWorker;
import com.inv.ui.util.DoubleFormat;
import com.inv.ui.util.WidgetUtil;
import com.inv.ui.widgets.CustomCellFactory;
import com.inv.ui.widgets.XTooltip;
import com.inv.xflux.entity.Currency;
import com.inv.xflux.entity.Employee;
import com.inv.xflux.entity.Order;
import com.inv.xflux.entity.Product;
import com.inv.xflux.model.*;
import com.xd.chart.DonutChart;
import com.xd.controls.XButton;
import com.xd.controls.XListView;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.Year;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author XDSSWAR
 */
@SuppressWarnings({"unchecked","Duplicates"})
public final class Home implements Initializable, IWidget {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label title1;

    @FXML
    private Label label1,earningOverviewLabel;

    @FXML
    private Label title2;

    @FXML
    private Label label2;

    @FXML
    private Label title3;

    @FXML
    private Label label3;

    @FXML
    private Label title4;

    @FXML
    private Label label4;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis catAxis;

    @FXML
    private NumberAxis numAxis;

    @FXML
    private XButton chartMenu;

    @FXML
    private ContextMenu chartContextMenu;

    @FXML
    private DonutChart donutChart;

    @FXML
    private XListView<Order> listView;

    @FXML
    private XButton refreshListBtn, newOrderBtn;

    private String actualYear;
    private String actualMonth;
    private OrderModel model;
    private Currency currency=null;
    private ClientModel clientModel=null;
    private final TextField searchText;

    /**
     * Constructor
     * @param searchText
     */
    public Home(TextField searchText){
        this.searchText=searchText;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model=new OrderModel();
        String[] CAL = {"January", "February","March", "April", "May", "June", "July",
                "August", "September", "October", "November","December"};
        currency=new CurrencyModel().getDefault();
        clientModel=new ClientModel();
        listView.setCellFactory(new CustomCellFactory().buildListCell());
        actualYear= Year.now().toString();
        Calendar calendar=Calendar.getInstance();
        actualMonth= CAL[calendar.get(Calendar.MONTH)];
        donutChart.setCenterColor(Color.WHITE);
        donutChart.setCenterStroke(55);
        donutChart.setCenterStrokeColor(Color.WHITE);
        initBarChart(actualYear,true);
        initDonutChart();
        loadLastedOrders();
        iniContextMenu();
        initLabels();
        refreshListBtn.setOnAction(event -> {
            loadLastedOrders();
        });
        newOrderBtn.setOnAction(event -> {
            WidgetUtil widgetUtil=new WidgetUtil();
            AnchorPane pane=(AnchorPane) scrollPane.getParent();
            Orders ordersController = new Orders(searchText,true);
            widgetUtil.addViewWithController(pane, "orders/orders",ordersController);
        });
        BgWorker.getInstance().scheduleAtFixedRate(()->{
            loadLastedOrders();
            initBarChart(actualYear,false);
            initDonutChart();
        },5,5, TimeUnit.MINUTES);
    }

    /**
     * Init labels
     */
    private void initLabels(){
        Runnable task=()-> {
            String monthAmount=currency.getSymbol()+DoubleFormat.sFormat(
                    model.getOrderAmountByMonth(actualMonth,actualYear,OrderStatus.PAID)
            );
            String yearAmount=currency.getSymbol()+DoubleFormat.sFormat(
                    model.getOrderAmountByYearAndStatus(actualYear,OrderStatus.PAID)
            );
            String orderQuantityByMonth=""+model.getOrderQuantityByMonth(actualMonth);
            String clientQuantity=""+clientModel.getAll().size();

            Platform.runLater(()-> {
                    title1.setText("EARNINGS ("+actualMonth.toUpperCase()+")");
                    label1.setText(monthAmount);
                    title2.setText("EARNINGS ("+actualYear+")");
                    label2.setText(yearAmount);
                    title3.setText("ORDERS ("+actualMonth.toUpperCase()+")");
                    label3.setText(orderQuantityByMonth);
                    label4.setText(clientQuantity);
            });
        };
        Thread labelThread= new Thread(task);
        labelThread.start();
    }

    /**
     * Init Bar chart Context Menu
     */
    private void iniContextMenu(){
        chartMenu.setOnMouseClicked(event -> {
            chartContextMenu.setOpacity(0);
            chartContextMenu.show(barChart, event.getScreenX()-event.getX(),
                    event.getScreenY()+(chartMenu.getHeight()-event.getY())+10);
            new Anima().fadeInContextMenu(chartContextMenu,300);
        });
        //Load all years and set then as MenuItems
        Runnable task=()->{
            ObservableList<MenuItem> items=FXCollections.observableArrayList();
            for (String s:OrderModel.getOrderYears()) {
                MenuItem menuItem=new MenuItem("Select "+s);
                menuItem.setId(s);
                items.add(menuItem);
            }
            if(items.size()>1) {
                for (MenuItem item : items) {
                    item.setOnAction(event -> {
                        actualYear=item.getId();
                        initBarChart(actualYear,true);
                    });
                }
              chartContextMenu.getItems().addAll(items);
            }
        };
        Thread contextThread=new Thread(task);
        contextThread.start();
    }

    /**
     * Init Bar Chart
     * @param year year
     * @param animated animated
     */
    private void initBarChart(String year,boolean animated){
        //Main Chart
        Runnable task = () -> {
            //Main Chart
            String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
            ObservableList<String> lists = FXCollections.observableArrayList();
            XYChart.Series<String, Number> paidSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> unpaidSeries = new XYChart.Series<>();
            Map<String,Double> paidMap=new HashMap<>();
            Map<String,Double> unpaidMap=new HashMap<>();
            for (int i=0; i<12;i++) {
                String month=months[i];
                String monthShort=month.substring(0,3);
                lists.add(monthShort);
                paidMap.put(monthShort,model.getOrderAmountByMonth(month, year, OrderStatus.PAID));
                unpaidMap.put(monthShort,model.getOrderAmountByMonth(month,year,OrderStatus.UNPAID));
                paidSeries.getData().add(new XYChart.Data<>(monthShort, paidMap.get(monthShort)));
                unpaidSeries.getData().add(new XYChart.Data<>(monthShort,unpaidMap.get(monthShort)));
            }
            Platform.runLater(()->{
                barChart.setAnimated(animated);
                barChart.getData().clear();
                earningOverviewLabel.setText("Earnings Overview " + year);
                paidSeries.setName("Earnings " + year);
                unpaidSeries.setName("Unpaid " + year);
                barChart.getData().add(paidSeries);
                barChart.getData().addAll(unpaidSeries);
                catAxis.getCategories().clear();
                barChart.getData().forEach(series -> {
                    series.getData().forEach(data -> {
                        if (data.getYValue().doubleValue()<=0){
                            data.getNode().setOpacity(0);
                        }
                        XTooltip tooltip = new XTooltip(
                                "Month: " + data.getXValue(),
                                "Earnings: "+currency.getSymbol() + paidMap.get(data.getXValue()),
                                "Unpaid: "+currency.getSymbol() + unpaidMap.get(data.getXValue()));
                        XTooltip.install(data.getNode(),tooltip);
                    });
                });
                barChart.setAnimated(false);
                catAxis.getCategories().addAll(lists);
            });
        };
        Thread barChartThread = new Thread(task);
        barChartThread.start();
    }


    /**
     * Init pie Chart
     */
    private void initDonutChart(){
        //Pie chart
        Runnable task = () -> {
            ObservableList<PieChart.Data> ordersByEmp = FXCollections.observableArrayList();
            EmployeeModel employeeModel=new EmployeeModel();
            ObservableList<Employee> empList=employeeModel.getAll();
            OrderModel orderModel=new OrderModel();
            int orderSize=0;
            for (Employee emp:empList){
                ObservableList<Order> orderList=orderModel.getAllByEmployee(emp);
                ordersByEmp.add(new PieChart.Data(emp.getUser(),orderList.size()));
                orderSize=orderSize+orderList.size();
            }
            int finalOrderSize = orderSize;
            Duration duration = Duration.millis(100);
            Platform.runLater(()-> {
                donutChart.setAnimated(true);
                Timeline timeline = new Timeline(new KeyFrame(duration, e -> {
                    if (donutChart.getData().size() > 0) {
                        donutChart.getData().clear();
                    }
                    donutChart.getData().addAll(ordersByEmp);
                    donutChart.getData().forEach(data -> {
                        double percent = (data.getPieValue() / finalOrderSize) * 100;
                        XTooltip tooltip = new XTooltip(data.getName(),
                                "Orders: " + (int) data.getPieValue(),
                                "Rate: " + DoubleFormat.sFormat(percent) + "%");
                        XTooltip.install(data.getNode(),tooltip);
                    });
                }));
                timeline.play();
            });
        };
        Thread pieChartThread = new Thread(task);
        pieChartThread.start();
    }



    /**
     * Load last 10 Orders
     * Dont modify this method
     */
    private void loadLastedOrders() {
        Runnable task = () -> {
            OrderModel ord=new OrderModel();
            ObservableList<Order> l=ord.getLast10(5);
            Duration duration = Duration.millis(50);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(listView.itemsProperty(),l, Interpolator.EASE_BOTH))
            );
            timeline.play();
            Platform.runLater(()-> {
                //Hack Around for java 11 dou to render issue
                listView.getItems().clear();
                listView.getItems().addAll(l);
            });
        };
        Thread productThread = new Thread(task);
        productThread.start();
    }
}
