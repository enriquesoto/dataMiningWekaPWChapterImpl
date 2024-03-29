package controllers;

import java.io.BufferedReader;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;

import main.Connect;
import model.Matricula;

import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

public class NotasController extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;
	
	private  Map<String, Object> session;
	private String username;
	public List<Double> notas;
	public Double grade;
	public Instance Curso;
	
	
	public Instance getCurso() {
		return Curso;
	}
	public void setCurso(Instance curso) {
		Curso = curso;
	}
	public Double getGrade() {
		return grade;
	}
	public void setGrade(Double grade) {
		this.grade = grade;
	}
	public List<Double> getNotas() {
		return notas;
	}
	public void setNotas(List<Double> notas) {
		this.notas = notas;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	public String misNotasParciales(){
		
		try {
			
			Instances data = new Instances(new BufferedReader(new
					FileReader("/home/enrique/estudio/maestria unsa/pw/datamininghcapter7/grades.arff")));
			data.setClassIndex(data.numAttributes() - 1);
			LinearRegression model = new LinearRegression();
			model.buildClassifier(data); //the last instance with missing
			System.out.println(model);
			//classify the last instance
			Curso = data.lastInstance();
			grade = model.classifyInstance(Curso);
			System.out.println("Nota para ("+Curso+"): "+grade);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String predecir(){	
		try {
			
			Instances data = new Instances(new BufferedReader(new
					FileReader("/home/enrique/estudio/maestria unsa/pw/datamininghcapter7/grades.arff")));
			data.setClassIndex(data.numAttributes() - 1);
			LinearRegression model = new LinearRegression();
			model.buildClassifier(data); //the last instance with missing
			System.out.println(model);
			//classify the last instance
			Curso = data.lastInstance();
			grade = model.classifyInstance(Curso);
			System.out.println("Nota para ("+Curso+"): "+grade);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String misNotasHistorico(){
		
		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		Connect db = Connect.getDbCon();
		//String username=request.getParameter("username"); 
	      String title = "Mis Notas";

	  	         ResultSet rss;
	  	         ResultSet rs;
	  		try {
	  			rss = db.query("SET @row=0");
	  			rs = db.query(
	  				/*	"select c.curricula_id, c.id, c.semestre, c.nombre, cp.turno, "
	  					+ "m.notaFinal, m.letras, c.creditos from matriculas m, "
	  					+ "cursos_programados cp, cursos c, usuarios u "
	  					+ "where m.cursos_programado_id = cp.id and "
	  					+ "cp.curso_id = c.id and m.usuario_id= u.id "
	  					+ "and u.username in ('20113639')");*/
	  					" select (@row:=@row+1) AS row, c.curricula_id, c.id, "
	  					+ "c.semestre div 2 as ano, c.semestre, c.nombre, cp.turno, "
	  					+ "m.notaFinal, m.letras, c.creditos "
	  					//+ "concat(cp.anho_dictado,'-',cp.ciclo_dictado as periodo) "
	  					+ "from matriculas m, cursos_programados cp, cursos c, "
	  					+ "usuarios u where m.cursos_programado_id = cp.id "
	  					+ "and cp.curso_id = c.id and m.usuario_id= u.id "
	  					+ "and u.username in ('"+username+"');");
	  			
	  			ArrayList<Matricula> al = new ArrayList<Matricula>();
	  			while (rs.next())
	  			{
//	  				 //Retrieve by column name
	  				Matricula mMatricula = new Matricula();
	  				mMatricula.setRow(rs.getInt("row"));
	  				mMatricula.setCurriculaId(rs.getInt("curricula_id"));
	  				mMatricula.setId(rs.getInt("curricula_id"));
	  				mMatricula.setAnho(rs.getInt("ano"));
	  				mMatricula.setSemestre(rs.getInt("semestre"));
	  				mMatricula.setNombre(rs.getString("nombre"));
	  				mMatricula.setTurno(rs.getString("turno"));
	  				mMatricula.setNotaFinal(rs.getInt("notaFinal"));
	  				mMatricula.setLetras(rs.getString("letras"));
	  				mMatricula.setCreditos(rs.getInt("creditos"));
	  				al.add(mMatricula);
//	  		
//	  		
	  			}
//	  			request.setAttribute("matriculaList", al);
//	  			getServletConfig().getServletContext().getRequestDispatcher("/notas.jsp").forward(request,response);

	  			}catch (SQLException e1) {
	  			// TODO Auto-generated catch block
	  			e1.printStackTrace();
	  		}
			
		return SUCCESS;
	}
		
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      
	      //System.out.println(jsonText.indexOf("("));
	      jsonText =jsonText.substring(jsonText.indexOf("(")+1);
	      
	      //System.out.println(jsonText);
	      
	      JSONObject json = new JSONObject(jsonText);
	      
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
