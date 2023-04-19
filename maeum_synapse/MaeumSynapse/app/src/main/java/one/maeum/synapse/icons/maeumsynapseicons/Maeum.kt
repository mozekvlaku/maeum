package one.maeum.synapse.icons.maeumsynapseicons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import one.maeum.synapse.icons.MaeumSynapseIcons

public val MaeumSynapseIcons.Maeum: ImageVector
    get() {
        if (_maeum != null) {
            return _maeum!!
        }
        _maeum = Builder(name = "Maeum", defaultWidth = 787.0.dp, defaultHeight = 787.0.dp,
                viewportWidth = 787.0f, viewportHeight = 787.0f).apply {
            group {
            }
            group {
            }
            group {
            }
        }
        .build()
        return _maeum!!
    }

private var _maeum: ImageVector? = null
