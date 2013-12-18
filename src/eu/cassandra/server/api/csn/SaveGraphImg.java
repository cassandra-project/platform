/*   
   Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.cassandra.server.api.csn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.Transformer;

import com.mongodb.DBObject;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.io.PajekNetWriter;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import eu.cassandra.server.IServletContextListener;

public class SaveGraphImg {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String saveImg(Vector<DBObject> nodes, Vector<DBObject> edges) throws IOException {
try {
		AggregateLayout<MyNode,String> aggrLayout = new AggregateLayout<MyNode,String>(new FRLayout<MyNode,String>(SparseMultigraph.<MyNode,String>getFactory().create()));
		Graph<MyNode, String> graph = aggrLayout.getGraph();
		HashMap<String,MyNode> n = new HashMap<String,MyNode>();
		for(DBObject node : nodes) {
			MyNode myNode = new MyNode(node.get("_id").toString(), (node.get("name")==null)?"":node.get("name").toString());
			n.put(node.get("_id").toString(),myNode);
			graph.addVertex(n.get(node.get("_id").toString() ) );
		}
		for(DBObject edge : edges) {
			graph.addEdge(edge.get("_id").toString() ,n.get(edge.get("node_id1").toString())  ,n.get(edge.get("node_id2").toString()) );
		}
		VisualizationViewer<MyNode,String> vv = new VisualizationViewer<MyNode,String>(aggrLayout);
		VisualizationImageServer<MyNode,String> vis = new VisualizationImageServer<MyNode,String>(vv.getGraphLayout(),
				vv.getGraphLayout().getSize());
		vis.setBackground(Color.WHITE);
		//vis.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		vis.getRenderContext().setEdgeLabelTransformer(new Transformer() {
		    public String transform(Object e) {
		        return "";
		    }
		});
		vis.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<MyNode, String>());
		vis.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<MyNode>());
		vis.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		BufferedImage image = (BufferedImage) vis.getImage(new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
				vv.getGraphLayout().getSize().getHeight() / 2),
				new Dimension(vv.getGraphLayout().getSize()));
		String fileName =  Calendar.getInstance().getTimeInMillis() + ".png";
		File outputfile = new File(IServletContextListener.graphs.getAbsolutePath()  + "/" +fileName);
		ImageIO.write(image, "png", outputfile);
		exportNetworkToFile(graph, fileName);
		return "/resources/graphs/" + fileName;
}catch(Exception e) {
	e.printStackTrace();
	return "";
}
	}


	private String exportNetworkToFile(Graph<MyNode, String> graph, String fileName) {
		try {
			PajekNetWriter<MyNode, String> writer = new PajekNetWriter<MyNode, String>();
			File outputfile = new File(IServletContextListener.graphs.getAbsolutePath()  + "/" +fileName + ".pajek");
			writer.save(graph, outputfile.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName + ".pajek";
	}
}
