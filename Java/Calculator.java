import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * A Java EE Calculator that works with Tomcat Servlet.
 * @author Jiajun Chen(Carson) jiajunc1
 */
@WebServlet("/Calculator")
public class Calculator extends HttpServlet {
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Method to handle all GET requests.
	 * @param request a HttpServletRequest object.
	 * @param response a HttpServletResponse object.
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) 
			throws ServletException, IOException {
		if (request.getParameter("operation") != null) {
			ArrayList<String> information = new ArrayList<String>();
			information.add("Please use POST method");
			sendResponse(response,
					information,
					request.getParameter("x"),
					request.getParameter("y"));
		} else {
			sendResponse(response, null, "0", "0");
		}
	}
	/**
	 * Method to handle all POST requests.
	 * @param request a HttpServletRequest object.
	 * @param response a HttpServletResponse object.
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {
		/**
		 * Get parameter values from the requests.
		 */
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		String operation = request.getParameter("operation");
		/**
		 * Initialize an arraylist for output information.
		 */
		ArrayList<String> information = new ArrayList<String>();
		/**
		 * If x is null, insert an error.
		 */
		if (x == null) {
			information.add("Unable to get X");
			x = "";
		}
		/**
		 * If y is null, insert an error.
		 */
		if (y == null) {
			information.add("Unable to get Y");
			y = "";
		}
		/**
		 * If operation is null, insert an error.
		 */
		if (operation == null) {
			information.add("Unable to get operation");
		}
		if (x != null && y != null && operation != null) {
			/**
			 * If x is not parseable to double, insert an error.
			 */
			if (!isParseable(x)) {
				information.add("X is not a number");
			}
			/**
			 * If y is not parseable to double, insert an error.
			 */
			if (!isParseable(y)) {
				information.add("Y is not a number");
			}
			/**
			 * If there is nothing in the information arraylist,
			 * which means there is no error so far,
			 * perform calculation.
			 */
			if (information.size() < 1) {
				double xnum = Double.parseDouble(x);
				double ynum = Double.parseDouble(y);
				double answer = 0.0;
				boolean error = false;
				/**
				 * Perform calculation based on the operator.
				 */
				switch (operation) {
				case "+":
					answer = xnum + ynum;
					break;
				case "-":
					answer = xnum - ynum;
					break;
				case "*":
					answer = xnum * ynum;
					break;
				case "/":
					if (ynum == 0.0) {
						information.add("Cannot divide by 0");
						error = true;
					} else {
						answer = xnum / ynum;
					}
					break;
				default:
					information.add("Unsupported operation");
					error = true;
					break;
				}
				/**
				 * If there is no error, format the answer.
				 */
				if (!error) {
					DecimalFormat formatter = new DecimalFormat("#,##0.00");
					String answerPrint = formatter.format(xnum)+" " + operation 
							+ " ("+formatter.format(ynum)+") = "+formatter.format(answer);
					information.add(answerPrint);
					sendResponse(response, information, x, y);
				} else {
					sendResponse(response, information, x, y);
				}
			} else {
				sendResponse(response, information, x, y);
			}
		} else {
			sendResponse(response, information, x, y);
		}
	}
	/**
	 * A method to detect whether a string is parseable to double or not.
	 * @param number a string that needs to parse.
	 * @return boolean value.
	 */
	private boolean isParseable(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	/**
	 * Format html response.
	 * @param response a HttpServletResponse object.
	 * @param information an arraylist of information.
	 * @param x the original string for x.
	 * @param y the original string for y.
	 * @throws IOException
	 */
	private void sendResponse(HttpServletResponse response,
			ArrayList<String> information,
			String x,
			String y) 
			throws IOException{
		 response.setContentType("text/html");
         response.setCharacterEncoding("UTF-8");
         PrintWriter out = response.getWriter();
         out.println("<!DOCTYPE html>");
         out.println("<html lang=\"en\">");
         out.println("<head>");
         out.println("<meta charset=\"UTF-8\">");
         out.println("<title>Calculator</title>");
         out.println("<link rel=\"stylesheet\" href=\"calculator.css\">");
         out.println("</head>");
         out.println("<body>");
         out.println("<header>");
         out.println("<div>");
         out.println("<h1>HW2 - Calculator</h1>");
         out.println("<h3>by Jiajun Chen</h3>");
         out.println("</div>");
         out.println("</header>");
         out.println("<form method=\"post\">");
         out.println("<label for=\"x\">X:");
         out.println("<input id=\"x\" type=\"text\" name=\"x\" value=\""+x+"\">");
         out.println("</label>");
         out.println("<label for=\"y\">Y:");
         out.println("<input id=\"y\" type=\"text\" name=\"y\" value=\""+y+"\">");
         out.println("</label>");
         out.println("<ul>");
         out.println("<li><button type=\"submit\" name=\"operation\" value=\"+\">+</button></li>");
         out.println("<li><button type=\"submit\" name=\"operation\" value=\"-\">-</button></li>");
         out.println("<li><button type=\"submit\" name=\"operation\" value=\"*\">*</button></li>");
         out.println("<li><button type=\"submit\" name=\"operation\" value=\"/\">/</button></li>");
         out.println("</ul>");
         if (information != null) {
        	 	out.println("<hr>");
             out.println("<label>");
             out.println("Output:");
             for (int i = 0; i < information.size(); i++) {
            	 	out.println("<input type=\"text\" value=\""+information.get(i)+"\" readonly>");
             }
             out.println("</label>");
         }
         out.println("</form>");
         out.println("</body>");
         out.println("</html>"); 
	}
}
