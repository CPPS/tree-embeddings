package nl.tue.cpps.lbend.tree;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.tue.cpps.lbend.geometry.Edge;
import nl.tue.cpps.lbend.geometry.Node;
import nl.tue.cpps.lbend.geometry.Tree;

@RequiredArgsConstructor
public final class CompactTreeWriter implements AutoCloseable {
    private final @NonNull DataOutputStream os;

    public void writeTree(Tree t) throws IOException {
        Collection<Node> nodes = t.getNodes();
        List<Edge> edges = t.edges();

        os.writeInt(nodes.size());
        os.writeInt(edges.size());
        for (Edge e : edges) {
            os.writeInt(e.getFrom());
            os.writeInt(e.getTo());
        }
    }

    @Override
    public void close() throws IOException {
        os.writeInt(-1);
        os.close();
    }

    public static void forFile(File out, IOConsumer<CompactTreeWriter> cb)
            throws IOException {
        try (
                OutputStream fos = new FileOutputStream(out);
                GZIPOutputStream zos = new GZIPOutputStream(fos);
                BufferedOutputStream bos = new BufferedOutputStream(zos);
                DataOutputStream dos = new DataOutputStream(bos);
                CompactTreeWriter w = new CompactTreeWriter(dos); //
        ) {
            cb.accept(w);
        }
    }
}
